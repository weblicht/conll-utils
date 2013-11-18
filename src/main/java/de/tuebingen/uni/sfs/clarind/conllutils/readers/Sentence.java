package de.tuebingen.uni.sfs.clarind.conllutils.readers;

import java.util.List;

/**
 * A sentence.
 * @author Daniël de Kok
 */
public interface Sentence {
    /**
     * Get the list of tokens in this sentence.
     * @return Token list.
     */
    public List<CONLLToken> getTokens();
}
