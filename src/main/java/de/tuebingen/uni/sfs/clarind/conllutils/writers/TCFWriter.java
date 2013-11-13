package de.tuebingen.uni.sfs.clarind.conllutils.writers;

import com.google.common.base.Optional;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLToken;
import eu.clarin.weblicht.wlfxb.tc.api.*;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusStored;
import eu.clarin.weblicht.wlfxb.xb.WLData;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * TCF corpus writer.
 *
 * @author Daniël de Kok
 */
public class TCFWriter implements CorpusWriter {
    private final Writer corpusWriter;
    private final TextCorpusStored corpus;
    private final SentencesLayer sentencesLayer;
    private final TokensLayer tokensLayer;
    private final PosTagsLayer posTagsLayer;
    private final LemmasLayer lemmasLayer;
    private final DependencyParsingLayer dependencyLayer;

    public TCFWriter(Writer writer, String language, boolean lemmas, Optional<String> posTagset,
                     Optional<String> dependencyTagset) {
        corpusWriter = writer;
        corpus = new TextCorpusStored(language);
        sentencesLayer = corpus.createSentencesLayer();
        tokensLayer = corpus.createTokensLayer();
        lemmasLayer = lemmas ? corpus.createLemmasLayer() : null;
        posTagsLayer = posTagset.isPresent() ? corpus.createPosTagsLayer(posTagset.get()) : null;
        dependencyLayer = dependencyTagset.isPresent() ?
                corpus.createDependencyParsingLayer(dependencyTagset.get(), true, false) : null;
    }

    @Override
    public void writeSentence(List<CONLLToken> sentence) throws IOException {
        List<PositionDependency> positionDependencies = new LinkedList<>();

        List<Token> tokens = new ArrayList<>();

        for (CONLLToken taggedToken : sentence) {
            Token token = tokensLayer.addToken(taggedToken.getForm());
            tokens.add(token);

            if (posTagsLayer != null)
                posTagsLayer.addTag(taggedToken.getPosTag().get(), token);

            if (lemmasLayer != null && taggedToken.getLemma().isPresent())
                lemmasLayer.addLemma(taggedToken.getLemma().get(), token);

            if (dependencyLayer != null && taggedToken.getHead().isPresent() && taggedToken.getDepRel().isPresent())
                positionDependencies.add(new PositionDependency(taggedToken.getDepRel().get(), taggedToken.getHead().get() - 1,
                        taggedToken.getPosition() - 1));
        }

        sentencesLayer.addSentence(tokens);

        if (dependencyLayer != null)
            addDependencies(tokens, positionDependencies, dependencyLayer);
    }

    private void addDependencies(List<Token> tokens, List<PositionDependency> positionDependencies, DependencyParsingLayer dependencyLayer) {
        List<Dependency> dependencies = new ArrayList<>();
        for (PositionDependency positionDependency : positionDependencies) {
            if (positionDependency.getGovenor() == -1)
                dependencies.add(dependencyLayer.createDependency(positionDependency.getRelation(),
                        tokens.get(positionDependency.getDependent())));
            else
                dependencies.add(dependencyLayer.createDependency(positionDependency.getRelation(),
                        tokens.get(positionDependency.getDependent()), tokens.get(positionDependency.getGovenor())));
        }

        dependencyLayer.addParse(dependencies);
    }

    @Override
    public void close() throws IOException {
        try {
            JAXBContext context = JAXBContext.newInstance(WLData.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(new WLData(corpus), corpusWriter);
        } catch (JAXBException e) {
            throw new IOException(e.getMessage());
        }
        corpusWriter.close();
    }

    private static class PositionDependency {
        private final String relation;
        private final int govenor;
        private final int dependent;

        private PositionDependency(String relation, int govenor, int dependent) {
            this.relation = relation;
            this.govenor = govenor;
            this.dependent = dependent;
        }

        private String getRelation() {
            return relation;
        }

        private int getGovenor() {
            return govenor;
        }

        private int getDependent() {
            return dependent;
        }
    }
}
