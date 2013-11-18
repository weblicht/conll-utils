package de.tuebingen.uni.sfs.clarind.conllutils.readers;

import org.apache.commons.lang3.StringUtils;

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

    @Override
    public String toString() {
        return StringUtils.join(tokens, '\n');
    }
}
