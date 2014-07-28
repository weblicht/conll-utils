package de.tuebingen.uni.sfs.clarind.conllutils.tuebadz;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.danieldk.nlp.conllx.CONLLToken;
import eu.danieldk.nlp.conllx.SimpleSentence;
import eu.danieldk.nlp.conllx.Sentence;
import eu.danieldk.nlp.conllx.Token;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Daniël de Kok <me@danieldk.eu>
 */
public final class ExpandMorph {
    public enum MorphAttribute {
        CASE,
        GENDER,
        NUMBER,
        MOOD,
        PERSON,
        TENSE
    }

    private static char UNDERSPECIFIED_SHORT = '*';

    private static String UNDERSPECIFIED_LONG = "underspecified";

    private static Map<String, List<MorphAttribute>> TAG_ATTRIBUTES =
            ImmutableMap.<String, List<MorphAttribute>>builder()
                    .put("ADJA", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER))
                    .put("APPR", ImmutableList.of(MorphAttribute.CASE))
                    .put("APPRART", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER))
                    .put("APPO", ImmutableList.of(MorphAttribute.CASE))
                    .put("ART", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER))
                    .put("NN", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER))
                    .put("NE", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER))
                    .put("PDS", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER))
                    .put("PDAT", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER))
                    .put("PIS", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER))
                    .put("PIAT", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER))
                    .put("PIDAT", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER))
                    .put("PPER", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER,
                            MorphAttribute.PERSON))
                    .put("PPOSS", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER))
                    .put("PPOSAT", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER))
                    .put("PRELS", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER))
                    .put("PRELAT", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER))
                    .put("PRF", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER,
                            MorphAttribute.PERSON))
                    .put("PWS", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER))
                    .put("PWAT", ImmutableList.of(MorphAttribute.CASE, MorphAttribute.NUMBER, MorphAttribute.GENDER))
                    .put("VAFIN", ImmutableList.of(MorphAttribute.PERSON, MorphAttribute.NUMBER, MorphAttribute.MOOD,
                            MorphAttribute.TENSE))
                    .put("VAIMP", ImmutableList.of(MorphAttribute.NUMBER))
                    .put("VMFIN", ImmutableList.of(MorphAttribute.PERSON, MorphAttribute.NUMBER, MorphAttribute.MOOD,
                            MorphAttribute.TENSE))
                    .put("VVFIN", ImmutableList.of(MorphAttribute.PERSON, MorphAttribute.NUMBER, MorphAttribute.MOOD,
                            MorphAttribute.TENSE))
                    .put("VVIMP", ImmutableList.of(MorphAttribute.NUMBER))
                    .build();

    private static Map<MorphAttribute, Map<Character, String>> MORPH_LONG_NAMES =
            ImmutableMap.<MorphAttribute, Map<Character, String>>builder()
                    .put(MorphAttribute.CASE, ImmutableMap.of('n', "nominative", 'g', "genitive", 'd', "dative",
                            'a', "accusative", UNDERSPECIFIED_SHORT, UNDERSPECIFIED_LONG))
                    .put(MorphAttribute.GENDER, ImmutableMap.of('m', "masculine", 'f', "feminine", 'n', "neuter",
                            UNDERSPECIFIED_SHORT, UNDERSPECIFIED_LONG))
                    .put(MorphAttribute.NUMBER, ImmutableMap.of('s', "singular", 'p', "plural",
                            UNDERSPECIFIED_SHORT, UNDERSPECIFIED_LONG))
                    .put(MorphAttribute.MOOD, ImmutableMap.of('i', "indicative", 'k', "subjunctive"))
                    .put(MorphAttribute.PERSON, ImmutableMap.of('1', "1", '2', "2", '3', "3",
                            UNDERSPECIFIED_SHORT, UNDERSPECIFIED_LONG))
                    .put(MorphAttribute.TENSE, ImmutableMap.of('s', "present", 't', "past"))
            .build();


    public static Sentence expandMorphology(Sentence sentence) {
        ImmutableList.Builder<Token> builder = ImmutableList.builder();

        for (Token token : sentence.getTokens()) {
            Optional<String> tag = token.getPosTag();
            Optional<String> morph = token.getFeatures();
            if (!tag.isPresent() || !morph.isPresent() || morph.get().equals("--")) {
                builder.add(token);
                continue;
            }

            List<MorphAttribute> attributes = TAG_ATTRIBUTES.get(tag.get());
            if (attributes == null) {
                builder.add(token);
                continue;
            }

            if (morph.get().length() != attributes.size()) {
                throw new IllegalArgumentException(String.format("Morphology has %d characters, expected %d: %s",
                        morph.get().length(), attributes.size(), token));
            }

            List<String> avs = new ArrayList<>();

            for (int i = 0; i < attributes.size(); ++i) {
                char morphShort = morph.get().charAt(i);
                String morphLong = MORPH_LONG_NAMES.get(attributes.get(i)).get(morphShort);
                avs.add(String.format("%s=%s", attributes.get(i).toString().toLowerCase(), morphLong));
            }

            // Add feature with combined morphology.
            avs.add(String.format("morph=%s", morph.get()));

            String newFeatures = StringUtils.join(avs, '|');

            builder.add(new CONLLToken(token.getID(), token.getForm(), token.getLemma(), token.getCoarsePOSTag(),
                    token.getPosTag(), Optional.of(newFeatures), token.getHead(), token.getDepRel(), token.getPHead(),
                    token.getPDepRel()));
        }

        return new SimpleSentence(builder.build());
    }

    private ExpandMorph() {}
}
