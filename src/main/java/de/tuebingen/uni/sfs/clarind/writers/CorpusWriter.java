package de.tuebingen.uni.sfs.clarind.writers;

import de.tuebingen.uni.sfs.clarind.readers.CONLLToken;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * Interface for corpus writers.
 * @author Daniël de Kok
 */
public interface CorpusWriter extends Closeable {
    /**
     * Write a sentence to the corpus.
     * @param sentence The sentence.
     * @throws IOException If the sentence could not be written.
     */
    public void writeSentence(List<CONLLToken> sentence) throws IOException;
}
