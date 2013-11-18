package de.tuebingen.uni.sfs.clarind.conllutils.cli;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLReader;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLToken;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.PlainSentence;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.Sentence;
import de.tuebingen.uni.sfs.clarind.conllutils.util.IOUtils;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.CONLLWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Replace a token, lemma, POS-tag, or dependency relation by another.
 */
public class Replace {
    private static final String PROGRAM_NAME = "replace";

    public static void main(String[] args) {
        if (args.length < 2)
            usage();

        Layer layer = valueOfLayer(args[1]);

        Map<String, String> replacements = getReplacements(args[0]);

        try (CONLLReader reader = new CONLLReader(IOUtils.openArgOrStdin(args, 2));
             CONLLWriter writer = new CONLLWriter(IOUtils.openArgOrStdout(args, 3))) {
            Sentence sentence;
            while ((sentence = reader.readSentence()) != null) {
                ImmutableList.Builder<CONLLToken> sentenceBuilder = ImmutableList.builder();

                for (CONLLToken token : sentence.getTokens()) {
                    CONLLToken newToken;
                    switch (layer) {
                        case TOKEN:
                            newToken = replaceToken(token, replacements);
                            break;
                        case LEMMA:
                            newToken = replaceLemma(token, replacements);
                            break;
                        case POSTAG:
                            newToken = replacePOSTag(token, replacements);
                            break;
                        case DEPENDENCY:
                            newToken = replaceDepRel(token, replacements);
                            break;
                        default:
                            throw new IllegalArgumentException("Unimplemented layer?");
                    }

                    sentenceBuilder.add(newToken);
                }

                writer.writeSentence(new PlainSentence(sentenceBuilder.build()));
            }
        } catch (FileNotFoundException e) {
            System.err.println(String.format("Could not open for reading: %s", args[0]));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static Map<String, String> getReplacements(String replacementsString) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        for (String replacementString: StringUtils.split(replacementsString, ',')) {
            String[] parts = StringUtils.split(replacementString, ':');
            if (parts.length != 2)
                throw new IllegalArgumentException(String.format("Replacement should be of the form 'ORIG:NEW': %s", replacementString));

            builder.put(parts[0], parts[1]);
        }

        return builder.build();
    }

    private static CONLLToken replaceDepRel(CONLLToken token, Map<String, String> replacements) {
        if (!token.getDepRel().isPresent())
            return token;

        String replacement = replacements.get(token.getDepRel().get());
        if (replacement == null)
            return token;
        else
            return new CONLLToken(token.getPosition(), token.getForm(), token.getLemma(),
                    token.getCoursePOSTag(), token.getPosTag(), token.getFeatures(), token.getHead(),
                    Optional.of(replacement), token.getPHead(), token.getPDepRel());
    }

    private static CONLLToken replacePOSTag(CONLLToken token, Map<String, String> replacements) {
        if (!token.getPosTag().isPresent())
            return token;

        String replacement = replacements.get(token.getPosTag().get());
        if (replacement == null)
            return token;
        else
            return new CONLLToken(token.getPosition(), token.getForm(), token.getLemma(),
                    token.getCoursePOSTag(), Optional.of(replacement), token.getFeatures(), token.getHead(),
                    token.getDepRel(), token.getPHead(), token.getPDepRel());
    }

    private static CONLLToken replaceLemma(CONLLToken token, Map<String, String> replacements) {
        if (!token.getLemma().isPresent())
            return token;

        String replacement = replacements.get(token.getLemma().get());
        if (replacement == null)
            return token;
        else
            return new CONLLToken(token.getPosition(), token.getForm(), Optional.of(replacement),
                    token.getCoursePOSTag(), token.getPosTag(), token.getFeatures(), token.getHead(),
                    token.getDepRel(), token.getPHead(), token.getPDepRel());
    }

    private static CONLLToken replaceToken(CONLLToken token, Map<String, String> replacements) {
        String replacement = replacements.get(token.getForm());
        if (replacement == null)
            return token;
        else
            return new CONLLToken(token.getPosition(), replacement, token.getLemma(), token.getCoursePOSTag(),
                    token.getPosTag(), token.getFeatures(), token.getHead(), token.getDepRel(),
                    token.getPHead(), token.getPDepRel());
    }

    public static void usage() {
        System.err.println(String.format("Usage: %s FIND:REPLACE[,FIND:REPLACE]... LAYER [CONLL] [CONLL_OUTPUT]", PROGRAM_NAME));
        System.exit(1);
    }

    private static enum Layer {TOKEN, LEMMA, POSTAG, DEPENDENCY}

    private static Layer valueOfLayer(String s) {
        return Layer.valueOf(s.toUpperCase());
    }
}
