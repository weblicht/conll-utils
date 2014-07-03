package de.tuebingen.uni.sfs.clarind.conllutils.writers;

import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLToken;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.Sentence;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A writer for RFTagger training files.
 *
 * @author Daniël de Kok <me@danieldk.eu>
 */
public class RFTaggerWriter extends AbstractCorpusWriter {
    private final BufferedWriter writer;

    private boolean firstSentence;

    private final boolean tuebaFormat;

    public RFTaggerWriter(BufferedWriter writer, boolean tuebaFormat) {
        this.writer = writer;
        this.tuebaFormat = tuebaFormat;
        firstSentence = true;
    }

    @Override
    public void write(Sentence sentence) throws IOException {
        if (firstSentence) {
            firstSentence = false;
        } else {
            writer.write("\n");
        }

        for (CONLLToken conllToken : sentence.getTokens()) {
            if (!conllToken.getPosTag().isPresent()) {
                throw new IllegalArgumentException("Input should contain fine-grained part-of-speech tags");
            }

            if (!conllToken.getFeatures().isPresent()) {
                throw new IllegalArgumentException("Input should contain (morphology) features");
            }

            List<String> morph = new ArrayList<>();
            morph.add(conllToken.getPosTag().get());

            String morphFeatures = conllToken.getFeatures().get();
            if (tuebaFormat) {
                // TüBA format: one character per morphological feature.

                if (conllToken.getPosTag().get().equals("APPR") && morphFeatures.equals("--")) {
                    morph.add("*");
                }
                else if (!morphFeatures.equals("--")) {
                    for (int i = 0; i < morphFeatures.length(); ++i) {
                        morph.add(Character.toString(morphFeatures.charAt(i)));
                    }
                }
            } else {
                Collections.addAll(morph, StringUtils.split(morphFeatures, '|'));
            }

            String rfMorph = StringUtils.join(morph, '.');

            writer.write(String.format("%s\t%s\n", conllToken.getForm(), rfMorph));
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
