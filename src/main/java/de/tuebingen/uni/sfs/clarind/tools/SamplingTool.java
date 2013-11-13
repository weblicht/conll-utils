package de.tuebingen.uni.sfs.clarind.tools;

import de.tuebingen.uni.sfs.clarind.readers.CONLLReader;
import de.tuebingen.uni.sfs.clarind.readers.CONLLToken;
import de.tuebingen.uni.sfs.clarind.readers.CorpusReader;
import de.tuebingen.uni.sfs.clarind.sample.ReservoirSampler;
import de.tuebingen.uni.sfs.clarind.writers.CONLLWriter;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.List;
import java.util.Random;

public class SamplingTool implements Tool {
    private static final String TOOL_NAME = "sample";
    private final String mainName;

    public SamplingTool(String mainName) {
        this.mainName = mainName;
    }

    @Override
    public String name() {
        return TOOL_NAME;
    }

    private Options programOptions() {
        Options options = new Options();
        options.addOption("n", "sample-size", true, "The size of the sample (default: 100)");
        options.addOption("s", "seed", true, "Seed for the random number generator (default: random)");
        return options;
    }

    @Override
    public void run(List<String> args) throws IOException {
        final Options options = programOptions();
        final CommandLine cmdLine = parseArguments(options, args);

        if (cmdLine.getArgs().length != 2)
            usage(options);

        final Random rng = cmdLine.hasOption('s') ? new Random(Long.parseLong(cmdLine.getOptionValue('s'))) : new Random();
        final int sampleSize = cmdLine.hasOption('n') ? Integer.parseInt(cmdLine.getOptionValue('n')) : 100;
        ReservoirSampler sampler = new ReservoirSampler(rng, sampleSize);

        final List<List<CONLLToken>> sample;
        sample = readAndSample(cmdLine.getArgs()[0], sampler);
        writeSample(cmdLine.getArgs()[1], sample);
    }

    private CommandLine parseArguments(Options options, List<String> args) {
        Parser parser = new GnuParser();
        String[] arr = new String[0];
        CommandLine cmdLine = null;
        try {
            cmdLine = parser.parse(options, args.toArray(arr));
        } catch (ParseException e) {
            usage(options);
        }
        return cmdLine;
    }

    private List<List<CONLLToken>> readAndSample(String filename, ReservoirSampler sampler) throws IOException {
        try (CorpusReader corpusReader = new CONLLReader(new BufferedReader(new FileReader(filename)))) {
            return sampler.sample(corpusReader);
        } catch (FileNotFoundException e) {
            throw new IOException(String.format("Could not open corpus for reading: %s", filename));
        } catch (IOException e) {
            throw new IOException(String.format("Error while reading: %s", filename));
        }
    }

    private void writeSample(String filename, List<List<CONLLToken>> sample) throws IOException {
        try (CONLLWriter writer = new CONLLWriter(new BufferedWriter(new FileWriter(filename)))) {
            for (List<CONLLToken> sentence : sample) {
                writer.writeSentence(sentence);
            }
        } catch (Exception e) {
            throw new IOException(String.format("Error writing sample: %s (%s)", filename, e.getMessage()));
        }
    }

    private void usage(Options options) {
        new HelpFormatter().printHelp(String.format("Usage: %s %s CONLL CONLL_SAMPLE", mainName, TOOL_NAME), options);
        System.exit(1);
    }
}
