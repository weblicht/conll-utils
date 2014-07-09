package de.tuebingen.uni.sfs.clarind.conllutils.cli;

import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLReader;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.CONLLWriter;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Program that merges multiple CONLL files in one CONLL file.
 *
 * @author Daniël de Kok <me@danieldk.eu>
 */
public class Merge {
    private static final String PROGRAM_NAME = "conll-merge";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("At least one input and output file should be specified.");
            usage();
        }

        List<String> inputs = Arrays.asList(args).subList(0, args.length - 1);
        try (CONLLWriter writer = new CONLLWriter(new BufferedWriter(new FileWriter(args[args.length - 1])))) {
            for (String input : inputs) {
                try (CONLLReader reader = new CONLLReader(new BufferedReader(new FileReader(input)))) {
                    writer.write(reader);
                }
            }
        } catch (IOException e) {
            System.err.printf("Error merging CONLL files: %s%n", e.getMessage());
        }
    }

    private static void usage() {
        System.err.printf("%s CONLL_INPUT ... CONLL_OUTPUT%n", PROGRAM_NAME);
        System.exit(1);
    }

    private Merge() {
    }
}
