package de.tuebingen.uni.sfs.clarind.conllutils.cli;

import de.tuebingen.uni.sfs.clarind.conllutils.readers.*;
import de.tuebingen.uni.sfs.clarind.conllutils.sample.ReservoirSampler;
import de.tuebingen.uni.sfs.clarind.conllutils.util.IOUtils;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.CONLLWriter;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Sample {
    private static final String PROGRAM_NAME = "conll-sample";

    public static void main(String[] args) {
        final Options options = programOptions();
        final CommandLine cmdLine = parseArguments(options, args);

        final Random rng = cmdLine.hasOption('s') ? new Random(Long.parseLong(cmdLine.getOptionValue('s'))) : new Random();
        final int sampleSize = cmdLine.hasOption('n') ? Integer.parseInt(cmdLine.getOptionValue('n')) : 100;
        ReservoirSampler sampler = new ReservoirSampler(rng, sampleSize);

        List<List<CONLLToken>> sample = null;
        try (CONLLReader reader = new CONLLReader(IOUtils.openArgOrStdin(cmdLine.getArgs(), 0))) {
            sample = sampler.sample(reader);
        } catch (IOException e) {
            System.err.println(String.format("Error reading CONLL corpus: %s", e.getMessage()));
            System.exit(1);
        }
        try (CONLLWriter writer = new CONLLWriter(IOUtils.openArgOrStdout(cmdLine.getArgs(), 1))) {
            writeSample(writer, sample);
        } catch (IOException e) {
            System.err.println(String.format("Error writing sample: %s", e.getMessage()));
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
        options.addOption("n", "sample-size", true, "The size of the sample (default: 100)");
        options.addOption("s", "seed", true, "Seed for the random number generator (default: random)");
        return options;
    }

    private static void writeSample(CONLLWriter writer, List<List<CONLLToken>> sample) throws IOException {
        for (List<CONLLToken> sentence : sample) {
            writer.writeSentence(new PlainSentence(sentence));
        }
    }

    private static void usage(Options options) {
        new HelpFormatter().printHelp(String.format("Usage: %s [OPTION]... [CONLL] [CONLL_SAMPLE]", PROGRAM_NAME), options);
        System.exit(1);
    }
}
