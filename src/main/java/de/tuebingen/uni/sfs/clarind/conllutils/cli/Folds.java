package de.tuebingen.uni.sfs.clarind.conllutils.cli;

import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLReader;
import de.tuebingen.uni.sfs.clarind.conllutils.util.IOUtils;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.CONLLWriter;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.CorpusWriter;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.PartitioningWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Program to create folds of CONLL data.
 */
public class Folds {
    public static void main(String[] args) throws IOException {
        if (args.length < 3)
            usage();

        final int nFolds = Integer.parseInt(args[0]);
        final String prefix = args[1];
        final String suffix = args[2];

        final List<CorpusWriter> writers = new ArrayList<>();
        for (int i = 0; i < nFolds; ++i) {
            try {
                writers.add(new CONLLWriter(new BufferedWriter(new FileWriter(String.format("%s%d%s", prefix, i, suffix)))));
            } catch (IOException e) {
                for (CorpusWriter writer : writers)
                    writer.close();

                System.err.println(e.getMessage());
            }
        }

        try (CONLLReader reader = new CONLLReader(IOUtils.openArgOrStdin(args, 3));
             PartitioningWriter writer = new PartitioningWriter(writers)) {
            writer.write(reader);
        }
    }

    private static void usage() {
        System.err.println("Usage: conll-folds N prefix suffix [CONLL]");
        System.exit(1);
    }
}
