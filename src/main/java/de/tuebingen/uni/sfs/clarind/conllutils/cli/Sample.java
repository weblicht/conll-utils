package de.tuebingen.uni.sfs.clarind.conllutils.cli;

import de.tuebingen.uni.sfs.clarind.conllutils.sample.ReservoirSampler;
import de.tuebingen.uni.sfs.clarind.conllutils.sample.Sampler;
import de.tuebingen.uni.sfs.clarind.conllutils.util.IOUtils;
import eu.danieldk.nlp.conllx.Sentence;
import eu.danieldk.nlp.conllx.SimpleSentence;
import eu.danieldk.nlp.conllx.Token;
import eu.danieldk.nlp.conllx.reader.*;
import eu.danieldk.nlp.conllx.writer.CONLLWriter;
import org.apache.commons.cli.*;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import java.io.IOException;
import java.util.List;

public class Sample {
    private static final String PROGRAM_NAME = "conll-sample";

    public static void main(String[] args) {
        Options options = programOptions();
        CommandLine cmdLine = parseArguments(options, args);

        RandomGenerator rng = cmdLine.hasOption('s') ?
                new MersenneTwister(Long.parseLong(cmdLine.getOptionValue('s'))) : new MersenneTwister();
        final int sampleSize = cmdLine.hasOption('n') ? Integer.parseInt(cmdLine.getOptionValue('n')) : 100;

        Sampler<List<Token>> sampler = new ReservoirSampler<>(sampleSize, rng);
        readAndSample(cmdLine, sampler);
        writeSample(cmdLine, sampler.sample());
    }

    private static void readAndSample(CommandLine cmdLine, Sampler<List<Token>> sampler) {
        try (CONLLReader reader = new CONLLReader(IOUtils.openArgOrStdin(cmdLine.getArgs(), 0))) {
            Sentence sentence;
            while ((sentence = reader.readSentence()) != null)
                sampler.add(sentence.getTokens());
        } catch (IOException e) {
            System.err.println(String.format("Error reading CONLL corpus: %s", e.getMessage()));
            System.exit(1);
        }
    }

    private static void writeSample(CommandLine cmdLine, Iterable<List<Token>> sample) {
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

    private static void writeSample(CONLLWriter writer, Iterable<List<Token>> sample) throws IOException {
        for (List<Token> sentence : sample) {
            writer.write(new SimpleSentence(sentence));
        }
    }

    private static void usage(Options options) {
        new HelpFormatter().printHelp(String.format("Usage: %s [OPTION]... [CONLL] [CONLL_SAMPLE]", PROGRAM_NAME), options);
        System.exit(1);
    }
}
