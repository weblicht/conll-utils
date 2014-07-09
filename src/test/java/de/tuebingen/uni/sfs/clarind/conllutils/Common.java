package de.tuebingen.uni.sfs.clarind.conllutils;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLToken;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.PlainSentence;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.Sentence;

public class Common {
    public static String TEST_FRAGMENT = "1\tDie\tdie\tART\tART\tnsf\t2\tDET\n" +
            "2\tGroßaufnahme\tGroßaufnahme\tN\tNN\tnsf\t0\tROOT\n\n" +
            "1\tGilles\tGilles\tN\tNE\tnsm\t0\tROOT\n" +
            "2\tDeleuze\tDeleuze\tN\tNE\tnsm\t1\tAPP";

    // Has two separating lines. Not according to the CoNLL standard, but we want to be able to
    // swallow it.
    public static String TEST_FRAGMENT_ROBUST = "1\tDie\tdie\tART\tART\tnsf\t2\tDET\n" +
            "2\tGroßaufnahme\tGroßaufnahme\tN\tNN\tnsf\t0\tROOT\n\n\n" +
            "1\tGilles\tGilles\tN\tNE\tnsm\t0\tROOT\n" +
            "2\tDeleuze\tDeleuze\tN\tNE\tnsm\t1\tAPP";

    public static String TEST_FRAGMENT_EMPTY_PROJ = "1\tDie\tdie\tART\tART\tnsf\t2\tDET\t_\t_\n" +
            "2\tGroßaufnahme\tGroßaufnahme\tN\tNN\tnsf\t0\tROOT\t_\t_\n\n" +
            "1\tGilles\tGilles\tN\tNE\tnsm\t0\tROOT\t_\t_\n" +
            "2\tDeleuze\tDeleuze\tN\tNE\tnsm\t1\tAPP\t_\t_\n";


    public static Sentence TEST_SENTENCE_1 = new PlainSentence(ImmutableList.of(
            new CONLLToken(1, "Die", Optional.of("die"), Optional.of("ART"), Optional.of("ART"), Optional.of("nsf"),
                    Optional.of(2), Optional.of("DET"), Optional.<Integer>absent(), Optional.<String>absent()),
            new CONLLToken(2, "Großaufnahme", Optional.of("Großaufnahme"), Optional.of("N"), Optional.of("NN"), Optional.of("nsf"),
                    Optional.of(0), Optional.of("ROOT"), Optional.<Integer>absent(), Optional.<String>absent())
    ));

    public static Sentence TEST_SENTENCE_2 = new PlainSentence(ImmutableList.of(
            new CONLLToken(1, "Gilles", Optional.of("Gilles"), Optional.of("N"), Optional.of("NE"), Optional.of("nsm"),
                    Optional.of(0), Optional.of("ROOT"), Optional.<Integer>absent(), Optional.<String>absent()),
            new CONLLToken(2, "Deleuze", Optional.of("Deleuze"), Optional.of("N"), Optional.of("NE"), Optional.of("nsm"),
                    Optional.of(1), Optional.of("APP"), Optional.<Integer>absent(), Optional.<String>absent())
    ));

    private Common() {}
}
