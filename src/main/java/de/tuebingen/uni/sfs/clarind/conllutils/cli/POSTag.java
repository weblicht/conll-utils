package de.tuebingen.uni.sfs.clarind.conllutils.cli;

import com.google.common.collect.ImmutableList;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLReader;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLToken;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.PlainSentence;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.Sentence;
import de.tuebingen.uni.sfs.clarind.conllutils.util.IOUtils;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.CONLLWriter;
import org.apache.commons.cli.*;

import java.io.*;

public class POSTag {
    private static final String PROGRAM_NAME = "postag";

    public static void main(String[] args) {
        Options options = programOptions();
        CommandLine cmdLine = parseArguments(options, args);

        checkArguments(cmdLine);

        try (CONLLReader reader = new CONLLReader(IOUtils.openArgOrStdin(cmdLine.getArgs(), 0));
             CONLLWriter writer = new CONLLWriter(IOUtils.openArgOrStdout(cmdLine.getArgs(), 1))) {
            Sentence sentence;
            while ((sentence = reader.readSentence()) != null) {
                ImmutableList.Builder<CONLLToken> builder = ImmutableList.builder();

                for (CONLLToken token : sentence.getTokens()) {
                    if (cmdLine.hasOption('c'))
                        builder.add(new CONLLToken(token.getPosition(), token.getForm(), token.getLemma(),
                                token.getCoursePOSTag(), token.getCoursePOSTag(), token.getFeatures(), token.getHead(),
                                token.getDepRel(), token.getPHead(), token.getPDepRel()));
                    else
                        builder.add(new CONLLToken(token.getPosition(), token.getForm(), token.getLemma(),
                                token.getPosTag(), token.getPosTag(), token.getFeatures(), token.getHead(),
                                token.getDepRel(), token.getPHead(), token.getPDepRel()));
                }

                writer.write(new PlainSentence(builder.build()));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void checkArguments(CommandLine cmdLine) {
        if (!cmdLine.hasOption('c') && !cmdLine.hasOption('f')) {
            System.err.println("Nothing to do!");
            System.exit(1);
        }

        if (cmdLine.hasOption('c') && cmdLine.hasOption('f')) {
            System.err.println("Cannot use both coarse-grained and fine-grained tags!");
            System.exit(1);
        }
    }

    private static CommandLine parseArguments(Options options, String[] args) {
        Parser parser = new GnuParser();
        CommandLine cmdLine = null;
        try {
            cmdLine = parser.parse(options, args);
        } catch (ParseException e) {
            usage(options);
        }
        return cmdLine;
    }

    private static Options programOptions() {
        Options options = new Options();
        options.addOption("c", "coarse", false, "Replace fine-grained postags by coarse-grained");
        options.addOption("f", "fine", false, "Replace coarse-grained postags by fine-grained");
        return options;
    }

    private static void usage(Options options) {
        new HelpFormatter().printHelp(String.format("Usage: %s [OPTIONS] [CONLL] [CONLL_OUT]", PROGRAM_NAME), options);
        System.exit(1);
    }
}
