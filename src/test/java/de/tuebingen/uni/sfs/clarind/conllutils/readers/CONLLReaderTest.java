package de.tuebingen.uni.sfs.clarind.conllutils.readers;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by daniel on 18/11/13.
 */
public class CONLLReaderTest {
    private static String TEST_FRAGMENT = "1\tDie\tdie\tART\tART\tnsf\t2\tDET\n" +
            "2\tGroßaufnahme\tGroßaufnahme\tN\tNN\tnsf\t0\tROOT\n\n" +
            "1\tGilles\tGilles\tN\tNE\tnsm\t0\tROOT\n" +
            "2\tDeleuze\tDeleuze\tN\tNE\tnsm\t1\tAPP";

    private static String TEST_FRAGMENT2= "1\n" +
            "2\n" +
            "1\n" +
            "2";

    private static Sentence TEST_SENTENCE_1 = new PlainSentence(ImmutableList.of(
            new CONLLToken(1, "Die", Optional.of("die"), Optional.of("ART"), Optional.of("ART"), Optional.of("nsf"),
                    Optional.of(2), Optional.of("DET"), Optional.<Integer>absent(), Optional.<String>absent()),
            new CONLLToken(2, "Großaufnahme", Optional.of("Großaufnahme"), Optional.of("N"), Optional.of("NN"), Optional.of("nsf"),
                    Optional.of(0), Optional.of("ROOT"), Optional.<Integer>absent(), Optional.<String>absent())
    ));

    private static Sentence TEST_SENTENCE_2 = new PlainSentence(ImmutableList.of(
            new CONLLToken(1, "Gilles", Optional.of("Gilles"), Optional.of("N"), Optional.of("NE"), Optional.of("nsm"),
                    Optional.of(0), Optional.of("ROOT"), Optional.<Integer>absent(), Optional.<String>absent()),
            new CONLLToken(2, "Deleuze", Optional.of("Deleuze"), Optional.of("N"), Optional.of("NE"), Optional.of("nsm"),
                    Optional.of(1), Optional.of("APP"), Optional.<Integer>absent(), Optional.<String>absent())
    ));

    @Test
    public void readSentenceTest() throws Exception {
        try (CorpusReader reader = new CONLLReader(new BufferedReader(new StringReader(TEST_FRAGMENT)))) {
            Assert.assertEquals(TEST_SENTENCE_1, reader.readSentence());
            Assert.assertEquals(TEST_SENTENCE_2, reader.readSentence());
            Assert.assertNull(reader.readSentence());
        }
    }

    @Test(expected = IOException.class)
    public void tooFewColumnsTest() throws Exception {
        try (CorpusReader reader = new CONLLReader(new BufferedReader(new StringReader(TEST_FRAGMENT2)))) {
            Assert.assertNull(reader.readSentence());
        }
    }
}
