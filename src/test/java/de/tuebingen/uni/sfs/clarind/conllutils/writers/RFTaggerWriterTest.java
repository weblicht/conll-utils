package de.tuebingen.uni.sfs.clarind.conllutils.writers;

import de.tuebingen.uni.sfs.clarind.conllutils.Common;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link de.tuebingen.uni.sfs.clarind.conllutils.writers.RFTaggerWriter}
 */
public class RFTaggerWriterTest {
    private static final String SENTENCE_1_2_FRAGMENT = "Die\tART.n.s.f\n" +
            "Großaufnahme\tNN.n.s.f\n" +
            "\n" +
            "Gilles\tNE.n.s.m\n" +
            "Deleuze\tNE.n.s.m\n";

    @Test
    public void writerTest() throws IOException {
        StringWriter stringWriter = new StringWriter();

        try (RFTaggerWriter rfTaggerWriter = new RFTaggerWriter(new BufferedWriter(stringWriter), true)) {
            rfTaggerWriter.write(Common.TEST_SENTENCE_1);
            rfTaggerWriter.write(Common.TEST_SENTENCE_2);
        }

        Assert.assertEquals(SENTENCE_1_2_FRAGMENT, stringWriter.toString());

    }

}