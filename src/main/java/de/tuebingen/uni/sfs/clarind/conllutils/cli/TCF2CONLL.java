package de.tuebingen.uni.sfs.clarind.conllutils.cli;

import de.tuebingen.uni.sfs.clarind.conllutils.readers.CorpusReader;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.TCFReader;
import de.tuebingen.uni.sfs.clarind.conllutils.util.IOUtils;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.CONLLWriter;
import eu.clarin.weblicht.wlfxb.io.WLFormatException;
import eu.clarin.weblicht.wlfxb.tc.xb.TextCorpusLayerTag;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.EnumSet;

/**
 * @author Daniël de Kok <me@danieldk.eu>
 */
public class TCF2CONLL {
    private static final String PROGRAM_NAME = "tcf2conll";

    public static void main(String[] args) {
        Options options = programOptions();
        CommandLine cmdLine = processArgs(options, args);

        EnumSet<TextCorpusLayerTag> layersToRead = EnumSet.of(TextCorpusLayerTag.SENTENCES, TextCorpusLayerTag.TOKENS);

        if (cmdLine.hasOption("dependency"))
            layersToRead.add(TextCorpusLayerTag.PARSING_DEPENDENCY);
        if (cmdLine.hasOption("postag"))
            layersToRead.add(TextCorpusLayerTag.POSTAGS);
        if (cmdLine.hasOption("morphology"))
            layersToRead.add(TextCorpusLayerTag.MORPHOLOGY);
        if (cmdLine.hasOption("lemma"))
            layersToRead.add(TextCorpusLayerTag.LEMMAS);

        try (CorpusReader tcfReader = new TCFReader(IOUtils.openArgOrStdinStream(cmdLine.getArgs(), 0), layersToRead);
             CONLLWriter conllWriter = new CONLLWriter(IOUtils.openArgOrStdout(cmdLine.getArgs(), 1))) {
            conllWriter.write(tcfReader);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static CommandLine processArgs(Options options, String[] args) {
        Parser parser = new GnuParser();
        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            usage(options);
        }

        return null; // Shouldn't happen.
    }

    private static Options programOptions() {
        Options options = new Options();
        options.addOption("d", "dependency", false, "Read dependency layer");
        options.addOption("p", "postag", false, "Read POS tag layer");
        options.addOption("l", "lemma", false, "Read lemma layer");
        options.addOption("m", "morphology", false, "Read morphology layer");
        return options;
    }

    private static void usage(Options options) {
        new HelpFormatter().printHelp(String.format("%s [OPTION]... TCF CONLL", PROGRAM_NAME), options);
        System.exit(1);
    }
}
