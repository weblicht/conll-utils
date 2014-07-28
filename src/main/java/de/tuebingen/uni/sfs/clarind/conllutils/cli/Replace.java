package de.tuebingen.uni.sfs.clarind.conllutils.cli;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.tuebingen.uni.sfs.clarind.conllutils.util.IOUtils;
import eu.danieldk.nlp.conllx.CONLLToken;
import eu.danieldk.nlp.conllx.Sentence;
import eu.danieldk.nlp.conllx.SimpleSentence;
import eu.danieldk.nlp.conllx.Token;
import eu.danieldk.nlp.conllx.reader.*;
import eu.danieldk.nlp.conllx.writer.CONLLWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Replace a token, lemma, POS-tag, or dependency relation by another.
 */
public class Replace {
    private static final String PROGRAM_NAME = "replace";

    public static void main(String[] args) {
        if (args.length < 2)
            usage();

        Layer layer = valueOfLayer(args[1]);

        Map<Pattern, String> replacements = getReplacements(args[0]);

        try (CONLLReader reader = new CONLLReader(IOUtils.openArgOrStdin(args, 2));
             CONLLWriter writer = new CONLLWriter(IOUtils.openArgOrStdout(args, 3))) {
            Sentence sentence;
            while ((sentence = reader.readSentence()) != null) {
                ImmutableList.Builder<Token> sentenceBuilder = ImmutableList.builder();

                for (Token token : sentence.getTokens()) {
                    Token newToken;
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
                        case FEATURES:
                            newToken = replaceFeatures(token, replacements);
                            break;
                        case DEPENDENCY:
                            newToken = replaceDepRel(token, replacements);
                            break;
                        default:
                            throw new IllegalArgumentException("Unimplemented layer?");
                    }

                    sentenceBuilder.add(newToken);
                }

                writer.write(new SimpleSentence(sentenceBuilder.build()));
            }
        } catch (FileNotFoundException e) {
            System.err.println(String.format("Could not open for reading: %s", args[0]));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static Map<Pattern, String> getReplacements(String replacementsString) {
        ImmutableMap.Builder<Pattern, String> builder = ImmutableMap.builder();

        for (String replacementString : StringUtils.split(replacementsString, ',')) {
            String[] parts = StringUtils.split(replacementString, ':');
            if (parts.length != 2)
                throw new IllegalArgumentException(String.format("Replacement should be of the form 'ORIG:NEW': %s", replacementString));

            builder.put(Pattern.compile(parts[0]), parts[1]);
        }

        return builder.build();
    }

    private static Token replaceDepRel(Token token, Map<Pattern, String> replacements) {
        if (!token.getDepRel().isPresent())
            return token;

        String replacement = findReplacement(replacements, token.getDepRel().get());
        if (replacement == null)
            return token;
        else
            return new CONLLToken(token.getID(), token.getForm(), token.getLemma(),
                    token.getCoarsePOSTag(), token.getPosTag(), token.getFeatures(), token.getHead(),
                    Optional.of(replacement), token.getPHead(), token.getPDepRel());
    }

    private static Token replacePOSTag(Token token, Map<Pattern, String> replacements) {
        if (!token.getPosTag().isPresent())
            return token;

        String replacement = findReplacement(replacements, token.getPosTag().get());
        if (replacement == null)
            return token;
        else
            return new CONLLToken(token.getID(), token.getForm(), token.getLemma(),
                    token.getCoarsePOSTag(), Optional.of(replacement), token.getFeatures(), token.getHead(),
                    token.getDepRel(), token.getPHead(), token.getPDepRel());
    }

    private static Token replaceLemma(Token token, Map<Pattern, String> replacements) {
        if (!token.getLemma().isPresent())
            return token;

        String replacement = findReplacement(replacements, token.getLemma().get());
        if (replacement == null)
            return token;
        else
            return new CONLLToken(token.getID(), token.getForm(), Optional.of(replacement),
                    token.getCoarsePOSTag(), token.getPosTag(), token.getFeatures(), token.getHead(),
                    token.getDepRel(), token.getPHead(), token.getPDepRel());
    }

    private static Token replaceToken(Token token, Map<Pattern, String> replacements) {
        if (!token.getForm().isPresent())
            return token;

        String replacement = findReplacement(replacements, token.getForm().get());
        if (replacement == null)
            return token;
        else
            return new CONLLToken(token.getID(), Optional.of(replacement), token.getLemma(),
                    token.getCoarsePOSTag(), token.getPosTag(), token.getFeatures(), token.getHead(), token.getDepRel(),
                    token.getPHead(), token.getPDepRel());
    }

    private static Token replaceFeatures(Token token, Map<Pattern, String> replacements) {
        if (!token.getFeatures().isPresent())
            return token;

        String replacement = findReplacement(replacements, token.getFeatures().get());

        if (replacement == null)
            return token;
        else
            return new CONLLToken(token.getID(), token.getForm(), token.getLemma(), token.getCoarsePOSTag(),
                    token.getPosTag(), Optional.of(replacement), token.getHead(), token.getDepRel(),
                    token.getPHead(), token.getPDepRel());
    }

    private static String findReplacement(Map<Pattern, String> replacements, String value) {
        String replacement = null;
        for (Map.Entry<Pattern, String> e : replacements.entrySet()) {
            if (e.getKey().matcher(value).matches()) {
                replacement = e.getValue();
                break;
            }
        }
        return replacement;
    }

    public static void usage() {
        System.err.println(String.format("Usage: %s FIND:REPLACE[,REGEXP:REPLACE]... LAYER [CONLL] [CONLL_OUTPUT]", PROGRAM_NAME));
        System.exit(1);
    }

    private static enum Layer {TOKEN, LEMMA, POSTAG, FEATURES, DEPENDENCY}

    private static Layer valueOfLayer(String s) {
        return Layer.valueOf(s.toUpperCase());
    }
}
