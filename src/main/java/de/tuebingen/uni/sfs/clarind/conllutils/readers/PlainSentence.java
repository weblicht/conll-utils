package de.tuebingen.uni.sfs.clarind.conllutils.readers;

import java.util.List;

/**
 * A sentence without any additional information.
 */
public class PlainSentence implements Sentence {
    private final List<CONLLToken> tokens;

    public PlainSentence(List<CONLLToken> tokens) {
        this.tokens = tokens;
    }

    @Override
    public List<CONLLToken> getTokens() {
        return tokens;
    }
}
