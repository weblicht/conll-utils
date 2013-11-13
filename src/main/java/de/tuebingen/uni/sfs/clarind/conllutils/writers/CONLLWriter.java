package de.tuebingen.uni.sfs.clarind.conllutils.writers;

import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLToken;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

/**
 * Writer for CONLL dependency, lemma and part-of-speech tag corpora.
 */
public class CONLLWriter implements CorpusWriter {
    private final BufferedWriter writer;
    private boolean firstSentence;

    public CONLLWriter(BufferedWriter writer) {
        firstSentence = true;
        this.writer = writer;
    }

    @Override
    public void writeSentence(List<CONLLToken> sentence) throws IOException {
        if (firstSentence)
            firstSentence = false;
        else
            writer.write("\n");

        for (CONLLToken token : sentence)
            writer.write(String.format("%s\n", token));
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
