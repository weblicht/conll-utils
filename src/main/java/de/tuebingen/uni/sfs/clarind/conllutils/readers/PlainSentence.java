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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlainSentence that = (PlainSentence) o;

        if (tokens != null ? !tokens.equals(that.tokens) : that.tokens != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return tokens != null ? tokens.hashCode() : 0;
    }
}
