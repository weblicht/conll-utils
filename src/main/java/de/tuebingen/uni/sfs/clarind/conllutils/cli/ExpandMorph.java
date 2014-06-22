package de.tuebingen.uni.sfs.clarind.conllutils.cli;

import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLReader;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.Sentence;
import de.tuebingen.uni.sfs.clarind.conllutils.util.IOUtils;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.CONLLWriter;

import java.io.IOException;

public class ExpandMorph {
    public static void main(String[] args) {
        try (CONLLReader reader = new CONLLReader(IOUtils.openArgOrStdin(args, 0));
             CONLLWriter writer = new CONLLWriter(IOUtils.openArgOrStdout(args, 1))) {
            Sentence sentence;
            while ((sentence = reader.readSentence()) != null) {
                writer.write(de.tuebingen.uni.sfs.clarind.conllutils.tuebadz.ExpandMorph.expandMorphology(sentence));

            }
        } catch (IOException e) {
            System.err.printf("Error reading or writing CoNLL: %s%n", e.getMessage());
            System.exit(1);
        }
    }
}
