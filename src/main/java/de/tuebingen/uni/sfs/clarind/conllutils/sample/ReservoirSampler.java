package de.tuebingen.uni.sfs.clarind.conllutils.sample;

import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLToken;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CorpusReader;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.Sentence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReservoirSampler {
    private final Random rng;

    private final int sampleSize;

    public ReservoirSampler(int sampleSize) {
        this.rng = new Random();
        this.sampleSize = sampleSize;
    }

    public ReservoirSampler(Random rng, int sampleSize) {
        this.rng = rng;
        this.sampleSize = sampleSize;
    }

    public List<List<CONLLToken>> sample(CorpusReader reader) throws IOException {
        List<List<CONLLToken>> sentences = new ArrayList<>(sampleSize);

        Sentence sentence;
        int idx = 0;
        while ((sentence = reader.readSentence()) != null) {
            if (sentences.size() < sampleSize)
                sentences.add(sentence.getTokens());
            else {
                // nextInt is exclusive, but a 'swap' with itself is also possible.
                int newPosition = rng.nextInt(idx + 1);

                if (newPosition < sampleSize)
                    sentences.set(newPosition, sentence.getTokens());
            }

            ++idx;
        }

        return sentences;
    }
}
