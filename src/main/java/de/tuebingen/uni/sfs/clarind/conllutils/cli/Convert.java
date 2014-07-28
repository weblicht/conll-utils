package de.tuebingen.uni.sfs.clarind.conllutils.cli;

import de.tuebingen.uni.sfs.clarind.conllutils.readers.BrownCorpusReader;
import eu.danieldk.nlp.conllx.reader.CorpusReader;
import eu.danieldk.nlp.conllx.writer.CONLLWriter;
import eu.danieldk.nlp.conllx.writer.CorpusWriter;

import java.io.*;

public class Convert {
    public static void main(String[] args) throws IOException {
        if (args.length != 2)
            usage();

        try (CorpusReader reader = new BrownCorpusReader(new BufferedReader(new FileReader(args[0])));
             CorpusWriter writer = new CONLLWriter(new BufferedWriter(new FileWriter(args[1])))) {
            writer.write(reader);
        }
    }

    private static void usage() {
        System.err.println("conll-convert other conll");
        System.exit(1);
    }
}
