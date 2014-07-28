package de.tuebingen.uni.sfs.clarind.conllutils.writers;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import de.tuebingen.uni.sfs.clarind.conllutils.Common;
import eu.danieldk.nlp.conllx.CONLLToken;
import eu.danieldk.nlp.conllx.SimpleSentence;
import eu.danieldk.nlp.conllx.Sentence;
import eu.danieldk.nlp.conllx.Token;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Unit tests for {@link de.tuebingen.uni.sfs.clarind.conllutils.writers.RFTaggerWriter}
 */
public class RFTaggerWriterTest {
    private static final String SENTENCE_1_2_FRAGMENT = "Die\tART.n.s.f\n" +
            "Großaufnahme\tNN.n.s.f\n" +
            "\n" +
            "Gilles\tNE.n.s.m\n" +
            "Deleuze\tNE.n.s.m\n";

    public static Sentence TEST_SENTENCE_1_INCORRECT_CARD = new SimpleSentence(ImmutableList.<Token>of(
            new CONLLToken(1, Optional.of("Die"), Optional.of("die"), Optional.of("ART"), Optional.of("ART"), Optional.of("ns"),
                    Optional.of(2), Optional.of("DET"), Optional.<Integer>absent(), Optional.<String>absent()),
            new CONLLToken(2, Optional.of("Großaufnahme"), Optional.of("Großaufnahme"), Optional.of("N"), Optional.of("NN"), Optional.of("nsf"),
                    Optional.of(0), Optional.of("ROOT"), Optional.<Integer>absent(), Optional.<String>absent())));

    @Test
    public void writerTest() throws IOException {
        StringWriter stringWriter = new StringWriter();

        try (RFTaggerWriter rfTaggerWriter = new RFTaggerWriter(new BufferedWriter(stringWriter), true, true)) {
            rfTaggerWriter.write(Common.TEST_SENTENCE_1);
            rfTaggerWriter.write(Common.TEST_SENTENCE_2);
        }

        Assert.assertEquals(SENTENCE_1_2_FRAGMENT, stringWriter.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void writerCardinalityTest() throws IOException {
        StringWriter stringWriter = new StringWriter();

        try (RFTaggerWriter rfTaggerWriter = new RFTaggerWriter(new BufferedWriter(stringWriter), true, true)) {
            rfTaggerWriter.write(Common.TEST_SENTENCE_1);
            rfTaggerWriter.write(Common.TEST_SENTENCE_2);
            rfTaggerWriter.write(TEST_SENTENCE_1_INCORRECT_CARD);
        }
    }

    @Test
    public void noWriterCardinalityTest() throws IOException {
        StringWriter stringWriter = new StringWriter();

        try (RFTaggerWriter rfTaggerWriter = new RFTaggerWriter(new BufferedWriter(stringWriter), true, false)) {
            rfTaggerWriter.write(Common.TEST_SENTENCE_1);
            rfTaggerWriter.write(Common.TEST_SENTENCE_2);
            rfTaggerWriter.write(TEST_SENTENCE_1_INCORRECT_CARD);
        }
    }
}