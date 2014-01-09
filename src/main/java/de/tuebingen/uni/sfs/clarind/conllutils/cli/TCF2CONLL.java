package de.tuebingen.uni.sfs.clarind.conllutils.cli;

import de.tuebingen.uni.sfs.clarind.conllutils.readers.CorpusReader;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.TCFReader;
import de.tuebingen.uni.sfs.clarind.conllutils.util.IOUtils;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.CONLLWriter;
import eu.clarin.weblicht.wlfxb.io.WLFormatException;
import org.apache.commons.cli.*;

import java.io.IOException;

/**
 * @author Daniël de Kok <me@danieldk.eu>
 */
public class TCF2CONLL {
    private static final String PROGRAM_NAME = "tcf2conll";

    public static void main(String[] args) {
        Options options = programOptions();
        CommandLine cmdLine = processArgs(options, args);

        //String language = cmdLine.hasOption('g') ? cmdLine.getOptionValue('g') : "de";
        //boolean lemmas = cmdLine.hasOption('l');
        //Optional<String> posTagset = Optional.fromNullable(cmdLine.getOptionValue('p'));
        //Optional<String> dependencyTagset = Optional.fromNullable(cmdLine.getOptionValue('d'));

        try (CorpusReader tcfReader = new TCFReader(IOUtils.openArgOrStdinStream(cmdLine.getArgs(), 0));
             CONLLWriter conllWriter = new CONLLWriter(IOUtils.openArgOrStdout(cmdLine.getArgs(), 1))) {
            conllWriter.write(tcfReader);
        } catch (IOException | WLFormatException e) {
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
        options.addOption("d", "dependency", true, "Read dependency layer");
        options.addOption("p", "postag", true, "Read POS tag layer");
        options.addOption("m", "morphology", true, "Read morphology layer");
        return options;
    }

    private static void usage(Options options) {
        new HelpFormatter().printHelp(String.format("%s [OPTION]... TCF CONLL", PROGRAM_NAME), options);
        System.exit(1);
    }
}
