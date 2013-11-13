package de.tuebingen.uni.sfs.clarind.conllutils.cli;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLReader;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLToken;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.CONLLWriter;

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
        if (args.length < 3)
            usage();

        Layer layer = valueOfLayer(args[2]);

        Map<String, String> replacements = getReplacements(args);

        try (CONLLReader reader = new CONLLReader(new BufferedReader(new FileReader(args[0])));
             CONLLWriter writer = new CONLLWriter(new BufferedWriter(new FileWriter(args[1])))) {
            List<CONLLToken> sentence;
            while ((sentence = reader.readSentence()) != null) {
                ImmutableList.Builder<CONLLToken> sentenceBuilder = ImmutableList.builder();

                for (CONLLToken token : sentence) {
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

                writer.writeSentence(sentenceBuilder.build());
            }
        } catch (FileNotFoundException e) {
            System.err.println(String.format("Could not open for reading: %s", args[0]));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static Map<String, String> getReplacements(String[] args) {
        List<String> replacementArgs = Arrays.asList(args).subList(3, args.length);
        if (replacementArgs.size() % 2 != 0)
            throw new IllegalArgumentException(
                    String.format("Missing replacement for: %s", replacementArgs.get(replacementArgs.size() - 1)));

        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (int i = 0; i < replacementArgs.size(); i += 2)
            builder.put(replacementArgs.get(i), replacementArgs.get(i + 1));

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
        System.err.println(String.format("Usage: %s CONLL CONLL_OUTPUT LAYER [FIND REPLACE]...", PROGRAM_NAME));
        System.exit(1);
    }

    private static enum Layer {TOKEN, LEMMA, POSTAG, DEPENDENCY}

    private static Layer valueOfLayer(String s) {
        return Layer.valueOf(s.toUpperCase());
    }
}
