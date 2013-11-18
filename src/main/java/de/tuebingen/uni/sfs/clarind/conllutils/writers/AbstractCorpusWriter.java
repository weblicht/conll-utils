package de.tuebingen.uni.sfs.clarind.conllutils.writers;

import de.tuebingen.uni.sfs.clarind.conllutils.readers.CorpusReader;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.Sentence;

import java.io.IOException;

/**
 * This abstract class provides an implementation
 * {@link CorpusWriter#write(de.tuebingen.uni.sfs.clarind.conllutils.readers.CorpusReader)},
 * that simply writes every sentence from the reader.
 */
public abstract class AbstractCorpusWriter implements CorpusWriter {
    @Override
    public void write(CorpusReader reader) throws IOException {
        Sentence sentence;
        while ((sentence = reader.readSentence()) != null)
            write(sentence);
    }
}
