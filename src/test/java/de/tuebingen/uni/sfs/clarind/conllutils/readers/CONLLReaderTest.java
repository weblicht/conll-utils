package de.tuebingen.uni.sfs.clarind.conllutils.readers;

import de.tuebingen.uni.sfs.clarind.conllutils.Common;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class CONLLReaderTest {
    public static String ERRONEOUS_FRAGMENT = "1\n" +
            "2\n" +
            "1\n" +
            "2";

    @Test
    public void readSentenceTest() throws Exception {
        try (CorpusReader reader = new CONLLReader(new BufferedReader(new StringReader(Common.TEST_FRAGMENT)))) {
            Assert.assertEquals(Common.TEST_SENTENCE_1, reader.readSentence());
            Assert.assertEquals(Common.TEST_SENTENCE_2, reader.readSentence());
            Assert.assertNull(reader.readSentence());
        }
    }


    @Test
    public void readSentenceRobustnessTest() throws Exception {
        try (CorpusReader reader = new CONLLReader(new BufferedReader(new StringReader(Common.TEST_FRAGMENT_ROBUST)))) {
            Assert.assertEquals(Common.TEST_SENTENCE_1, reader.readSentence());
            Assert.assertEquals(Common.TEST_SENTENCE_2, reader.readSentence());
            Assert.assertNull(reader.readSentence());
        }
    }

    @Test(expected = IOException.class)
    public void tooFewColumnsTest() throws Exception {
        try (CorpusReader reader = new CONLLReader(new BufferedReader(new StringReader(ERRONEOUS_FRAGMENT)))) {
            Assert.assertNull(reader.readSentence());
        }
    }
}
