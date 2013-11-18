package de.tuebingen.uni.sfs.clarind.conllutils.writers;

import de.tuebingen.uni.sfs.clarind.conllutils.Common;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

public class CONLLWriterTest {
    @Test
    public void writeTest() throws IOException{
        StringWriter sw = new StringWriter();
        try (CONLLWriter writer = new CONLLWriter(new BufferedWriter(sw))) {
            writer.write(Common.TEST_SENTENCE_1);
            writer.write(Common.TEST_SENTENCE_2);
        }

        Assert.assertEquals(Common.TEST_FRAGMENT_EMPTY_PROJ, sw.toString());
    }
}
