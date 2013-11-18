package de.tuebingen.uni.sfs.clarind.conllutils.writers;

import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLToken;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CorpusReader;
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
     * Write all the sentences from a {@link CorpusReader}. The reader will
     * <b>not</b> be closed.
     * @param reader The reader.
     * @throws IOException
     */
    public void write(CorpusReader reader) throws IOException;

    /**
     * Write a sentence to the corpus.
     * @param sentence The sentence.
     * @throws IOException If the sentence could not be written.
     */
    public void write(Sentence sentence) throws IOException;
}
