package de.tuebingen.uni.sfs.clarind.conllutils;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import eu.danieldk.nlp.conllx.CONLLToken;
import eu.danieldk.nlp.conllx.SimpleSentence;
import eu.danieldk.nlp.conllx.Sentence;
import eu.danieldk.nlp.conllx.Token;

public class Common {
    public static Sentence TEST_SENTENCE_1 = new SimpleSentence(ImmutableList.<Token>of(
            new CONLLToken(1, Optional.of("Die"), Optional.of("die"), Optional.of("ART"), Optional.of("ART"), Optional.of("nsf"),
                    Optional.of(2), Optional.of("DET"), Optional.<Integer>absent(), Optional.<String>absent()),
            new CONLLToken(2, Optional.of("Großaufnahme"), Optional.of("Großaufnahme"), Optional.of("N"), Optional.of("NN"), Optional.of("nsf"),
                    Optional.of(0), Optional.of("ROOT"), Optional.<Integer>absent(), Optional.<String>absent())
    ));

    public static Sentence TEST_SENTENCE_2 = new SimpleSentence(ImmutableList.<Token>of(
            new CONLLToken(1, Optional.of("Gilles"), Optional.of("Gilles"), Optional.of("N"), Optional.of("NE"), Optional.of("nsm"),
                    Optional.of(0), Optional.of("ROOT"), Optional.<Integer>absent(), Optional.<String>absent()),
            new CONLLToken(2, Optional.of("Deleuze"), Optional.of("Deleuze"), Optional.of("N"), Optional.of("NE"), Optional.of("nsm"),
                    Optional.of(1), Optional.of("APP"), Optional.<Integer>absent(), Optional.<String>absent())
    ));

    private Common() {}
}
