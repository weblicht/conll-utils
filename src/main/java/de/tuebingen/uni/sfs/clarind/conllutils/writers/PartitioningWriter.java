package de.tuebingen.uni.sfs.clarind.conllutils.writers;

import com.google.common.collect.ImmutableList;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CorpusReader;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.Sentence;

import java.io.IOException;
import java.util.List;

public class PartitioningWriter implements CorpusWriter {
    private int fold;

    private final List<CorpusWriter> writers;

    public PartitioningWriter(List<CorpusWriter> writers) {
        this.writers = ImmutableList.copyOf(writers);
        this.fold = 0;
    }

    @Override
    public void writeSentence(Sentence sentence) throws IOException {
        if (fold == writers.size())
            fold = 0;

        writers.get(fold++).writeSentence(sentence);
    }

    public void write(CorpusReader reader) throws IOException {
        Sentence sentence;
        while ((sentence = reader.readSentence()) != null)
            writeSentence(sentence);
    }

    @Override
    public void close() throws IOException {
        for (CorpusWriter writer : writers)
            writer.close();
    }
}
