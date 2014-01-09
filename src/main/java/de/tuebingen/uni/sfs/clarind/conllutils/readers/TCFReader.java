package de.tuebingen.uni.sfs.clarind.conllutils.readers;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.clarin.weblicht.wlfxb.io.TextCorpusStreamed;
import eu.clarin.weblicht.wlfxb.io.WLFormatException;
import eu.clarin.weblicht.wlfxb.tc.api.*;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusLayerTag;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * @author Daniël de Kok <me@danieldk.eu>
 */
public class TCFReader implements CorpusReader {
    private final TextCorpus textCorpus;

    private final SentencesLayer sentencesLayer;

    private final PosTagsLayer posTagsLayer;

    private final DependencyParsingLayer dependencyParsingLayer;

    private final MorphologyLayer morphologyLayer;

    private int currentSentence = 0;

    public TCFReader(InputStream tcfStream) throws WLFormatException {
        EnumSet<TextCorpusLayerTag> layersToRead = EnumSet.of(TextCorpusLayerTag.SENTENCES, TextCorpusLayerTag.TOKENS,
                TextCorpusLayerTag.POSTAGS, TextCorpusLayerTag.PARSING_DEPENDENCY, TextCorpusLayerTag.MORPHOLOGY);
        textCorpus = new TextCorpusStreamed(tcfStream, layersToRead);

        sentencesLayer = textCorpus.getSentencesLayer();

        posTagsLayer = textCorpus.getPosTagsLayer();

        dependencyParsingLayer = textCorpus.getDependencyParsingLayer();

        morphologyLayer = textCorpus.getMorphologyLayer();
    }

    public Map<Integer, RelationGovenor> extractDependencies(DependencyParse parse, Map<Token, Integer> tokenPositions) {
        ImmutableMap.Builder<Integer, RelationGovenor> depBuilder = ImmutableMap.builder();

        for (Dependency dependency : parse.getDependencies()) {
            Token[] dependants = dependencyParsingLayer.getDependentTokens(dependency);
            Token[] governors = dependencyParsingLayer.getGovernorTokens(dependency);

            if (dependants == null || governors == null)
                continue;

            for (Token dep : dependants)
                for (Token gov : governors) {
                    Integer depIdx = tokenPositions.get(dep);
                    if (depIdx == null)
                        throw new RuntimeException(String.format("Dependent in relation is not in the corresponding sentence: %s", dep));

                    Integer govIdx = tokenPositions.get(gov);
                    if (govIdx == null)
                        throw new RuntimeException(String.format("Governor in relation is not in the corresponding sentence: %s", gov));

                    depBuilder.put(depIdx, new RelationGovenor(dependency.getFunction(), govIdx));
                }
        }

        return depBuilder.build();
    }

    private Map<Token, Integer> getTokenPositions(Token[] sentenceTokens) {
        // Note: hashCode() and equals() is not implemented for Token.*, so we are relying on
        // Object's implementation, and the reference is used. This is bad, but works in this
        // case, because a Token uniquely identifies a token in the wlbfx.

        ImmutableMap.Builder<Token, Integer> builder = ImmutableMap.builder();

        for (int i = 0; i < sentenceTokens.length; ++i)
            builder.put(sentenceTokens[i], i);

        return builder.build();
    }

    @Override
    public Sentence readSentence() throws IOException {
        if (currentSentence == sentencesLayer.size())
            return null;

        eu.clarin.weblicht.wlfxb.tc.api.Sentence tcfSentence = sentencesLayer.getSentence(currentSentence);
        Token[] tcfTokens = sentencesLayer.getTokens(tcfSentence);
        Map<Token, Integer> tokenPositions = getTokenPositions(tcfTokens);
        DependencyParse parse = dependencyParsingLayer.getParse(currentSentence);
        Map<Integer, RelationGovenor> dependencies = extractDependencies(parse, tokenPositions);

        ImmutableList.Builder<CONLLToken> tokensBuilder = ImmutableList.builder();
        for (int i = 0; i < tcfTokens.length; i++) {
            Token tcfToken = tcfTokens[i];
            PosTag posTag = posTagsLayer.getTag(tcfToken);

            Optional<String> features = flatMorphology(tcfToken);

            RelationGovenor relationGovenor = dependencies.get(i);

            if (relationGovenor == null)
                tokensBuilder.add(new CONLLToken(i + 1, tcfToken.getString(), Optional.<String>absent(),
                        Optional.of(posTag.getString()), Optional.of(posTag.getString()), features,
                        Optional.of(0), Optional.of("ROOT"),
                        Optional.<Integer>absent(), Optional.<String>absent()));
            else
                tokensBuilder.add(new CONLLToken(i + 1, tcfToken.getString(), Optional.<String>absent(),
                        Optional.of(posTag.getString()), Optional.of(posTag.getString()), features,
                        Optional.of(relationGovenor.getGovernor() + 1), Optional.of(relationGovenor.getRelation()),
                        Optional.<Integer>absent(), Optional.<String>absent()));
        }

        ++currentSentence;

        return new PlainSentence(tokensBuilder.build());
    }

    // Todo: subfeatures?
    private Optional<String> flatMorphology(Token tcfToken) {
        MorphologyAnalysis morphAnalysis = morphologyLayer.getAnalysis(tcfToken);

        if (morphAnalysis == null)
            return Optional.absent();

        List<String> featureValues = new ArrayList<>();
        for (Feature feature : morphAnalysis.getFeatures())
            featureValues.add(String.format("%s:%s", StringUtils.replaceChars(feature.getName(), ' ', '_'),
                    StringUtils.replaceChars(feature.getValue(), ' ', '+')));

        return Optional.of(StringUtils.join(featureValues, '|'));
    }

    @Override
    public void close() throws IOException {
    }

    private class RelationGovenor {
        private final String relation;

        private final int governor;

        private RelationGovenor(String relation, int governor) {
            this.relation = relation;
            this.governor = governor;
        }

        public String getRelation() {
            return relation;
        }

        public int getGovernor() {
            return governor;
        }
    }
}
