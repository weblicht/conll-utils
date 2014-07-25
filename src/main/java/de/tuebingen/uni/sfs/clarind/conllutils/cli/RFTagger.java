package de.tuebingen.uni.sfs.clarind.conllutils.cli;

import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLReader;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CorpusReader;
import de.tuebingen.uni.sfs.clarind.conllutils.util.IOUtils;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.CorpusWriter;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.RFTaggerWriter;
import org.apache.commons.cli.*;

import java.io.IOException;

/**
 * CoNLL to RFTagger conversion
 *
 * @author Daniël de Kok <me@danieldk.eu>
 */
public class RFTagger {
    private static final String PROGRAM_NAME = "conll2rftagger";

    public static void main(String[] args) {
        Options options = programOptions();
        CommandLine cmdLine = processArgs(options, args);

        try (CorpusReader conllReader = new CONLLReader(IOUtils.openArgOrStdin(cmdLine.getArgs(), 0));
             CorpusWriter rfWriter = new RFTaggerWriter(IOUtils.openArgOrStdout(cmdLine.getArgs(), 1),
                     cmdLine.hasOption('t'), !cmdLine.hasOption('n'))) {
            rfWriter.write(conllReader);
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
        options.addOption("n", "non-strict", false, "Do not check cardinalities");
        options.addOption("t", "tuebadz", false, "TüBA-D/Z-style morphology");
        return options;
    }

    private static void usage(Options options) {
        new HelpFormatter().printHelp(String.format("%s [OPTION]... CONLL RF", PROGRAM_NAME), options);
        System.exit(1);
    }
}
