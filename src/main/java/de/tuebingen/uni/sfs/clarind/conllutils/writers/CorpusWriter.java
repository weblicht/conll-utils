package de.tuebingen.uni.sfs.clarind.conllutils.writers;

import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLToken;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.Sentence;

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
    public void writeSentence(Sentence sentence) throws IOException;
}
