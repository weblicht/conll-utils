package de.tuebingen.uni.sfs.clarind.conllutils.cli;

import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLReader;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLToken;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CorpusReader;
import de.tuebingen.uni.sfs.clarind.conllutils.sample.ReservoirSampler;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.CONLLWriter;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.List;
import java.util.Random;

public class Sample {
    private static final String PROGRAM_NAME = "conll-sample";

    public static void main(String[] args) {
        final Options options = programOptions();
        final CommandLine cmdLine = parseArguments(options, args);

        if (cmdLine.getArgs().length != 2)
            usage(options);

        final Random rng = cmdLine.hasOption('s') ? new Random(Long.parseLong(cmdLine.getOptionValue('s'))) : new Random();
        final int sampleSize = cmdLine.hasOption('n') ? Integer.parseInt(cmdLine.getOptionValue('n')) : 100;
        ReservoirSampler sampler = new ReservoirSampler(rng, sampleSize);

        List<List<CONLLToken>> sample = null;
        try {
            sample = readAndSample(cmdLine.getArgs()[0], sampler);
        } catch (IOException e) {
            System.err.println(String.format("Error reading CONLL corpus: %s", e.getMessage()));
            System.exit(1);
        }
        try {
            writeSample(cmdLine.getArgs()[1], sample);
        } catch (IOException e) {
            System.err.println(String.format("Error writing CONLL corpus: %s", e.getMessage()));
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

    private static List<List<CONLLToken>> readAndSample(String filename, ReservoirSampler sampler) throws IOException {
        try (CorpusReader corpusReader = new CONLLReader(new BufferedReader(new FileReader(filename)))) {
            return sampler.sample(corpusReader);
        } catch (FileNotFoundException e) {
            throw new IOException(String.format("Could not open corpus for reading: %s", filename));
        } catch (IOException e) {
            throw new IOException(String.format("Error while reading: %s", filename));
        }
    }

    private static void writeSample(String filename, List<List<CONLLToken>> sample) throws IOException {
        try (CONLLWriter writer = new CONLLWriter(new BufferedWriter(new FileWriter(filename)))) {
            for (List<CONLLToken> sentence : sample) {
                writer.writeSentence(sentence);
            }
        } catch (Exception e) {
            throw new IOException(String.format("Error writing sample: %s (%s)", filename, e.getMessage()));
        }
    }

    private static void usage(Options options) {
        new HelpFormatter().printHelp(String.format("Usage: %s [OPTION]... CONLL CONLL_SAMPLE", PROGRAM_NAME), options);
        System.exit(1);
    }
}
