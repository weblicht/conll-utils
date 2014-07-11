package de.tuebingen.uni.sfs.clarind.conllutils.cli;

import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLReader;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.CONLLWriter;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
        try (CONLLWriter writer = new CONLLWriter(new BufferedWriter(writerOrGZipWriter(new File(args[args.length - 1]))))) {
            for (String input : inputs) {
                try (CONLLReader reader = new CONLLReader(new BufferedReader(readerOrGZipReader(new File(input))))) {
                    writer.write(reader);
                }
            }
        } catch (IOException e) {
            System.err.printf("Error merging CONLL files: %s%n", e.getMessage());
        }
    }

    private static Reader readerOrGZipReader(File file) throws IOException {
        if (file.getName().endsWith(".gz")) {
            return new InputStreamReader(new GZIPInputStream(new FileInputStream(file)));
        }

        return new FileReader(file);
    }

    private static Writer writerOrGZipWriter(File file) throws IOException {
        if (file.getName().endsWith(".gz")) {
            return new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file)));
        }

        return new FileWriter(file);
    }

    private static void usage() {
        System.err.printf("%s CONLL_INPUT ... CONLL_OUTPUT%n", PROGRAM_NAME);
        System.exit(1);
    }

    private Merge() {
    }
}
