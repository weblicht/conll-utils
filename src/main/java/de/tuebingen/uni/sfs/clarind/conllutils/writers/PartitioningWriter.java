package de.tuebingen.uni.sfs.clarind.conllutils.writers;

import com.google.common.collect.ImmutableList;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.Sentence;

import java.io.IOException;
import java.util.List;

/**
 * This writer writes sentences to one or more {@link CorpusWriter}s. It can
 * be used to prepare data for e.g. n-fold cross-validation. The sentences are
 * written in an interspersed. E.g. if there are two writers, the distribution
 * is as follows:
 * <p/>
 * <ul>
 * <li>Sentence 1 → writer 1</li>
 * <li>Sentence 2 → writer 2</li>
 * <li>Sentence 3 → writer 1</li>
 * <li>Sentence 4 → writer 2</li>
 * <li>Etc.</li>
 * </ul>
 */
public class PartitioningWriter extends AbstractCorpusWriter {
    // The next fold/part.
    private int fold;

    private final List<CorpusWriter> writers;

    /**
     * Construct a {@link PartitioningWriter} using a list of writers. The list should
     * contain at least one element. Sentences are distributed among these writers.
     *
     * @param writers The writers.
     */
    public PartitioningWriter(List<CorpusWriter> writers) {
        if (writers.size() < 1)
            throw new IllegalArgumentException("We need at least one writer to write to");

        this.writers = ImmutableList.copyOf(writers);
        this.fold = 0;
    }

    @Override
    public void write(Sentence sentence) throws IOException {
        if (fold == writers.size())
            fold = 0;

        writers.get(fold++).write(sentence);
    }

    @Override
    public void close() throws IOException {
        for (CorpusWriter writer : writers)
            writer.close();
    }
}
