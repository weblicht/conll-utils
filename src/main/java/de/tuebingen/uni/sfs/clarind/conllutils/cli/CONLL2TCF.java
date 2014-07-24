package de.tuebingen.uni.sfs.clarind.conllutils.cli;

import com.google.common.base.Optional;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLReader;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CorpusReader;
import de.tuebingen.uni.sfs.clarind.conllutils.util.IOUtils;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.TCFWriter;
import org.apache.commons.cli.*;

import java.io.IOException;

public class CONLL2TCF {
    private static final String PROGRAM_NAME = "conll2tcf";

    public static void main(String[] args) {
        Options options = programOptions();
        CommandLine cmdLine = processArgs(options, args);

        String language = cmdLine.hasOption('g') ? cmdLine.getOptionValue('g') : "de";
        boolean lemmas = cmdLine.hasOption('l');
        Optional<String> posTagset = Optional.fromNullable(cmdLine.getOptionValue('p'));
        Optional<String> dependencyTagset = Optional.fromNullable(cmdLine.getOptionValue('d'));

        try (CorpusReader conllReader = new CONLLReader(IOUtils.openArgOrStdin(cmdLine.getArgs(), 0));
             TCFWriter tcfWriter = new TCFWriter(IOUtils.openArgOrStdout(cmdLine.getArgs(), 1), language,
                     lemmas, posTagset, dependencyTagset)) {
            tcfWriter.write(conllReader);
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
        options.addOption("g", "language", true, "The two-letter language code (default: de)");
        options.addOption("d", "dependency", true, "Create dependency layer, with the given tagset");
        options.addOption("p", "postag", true, "Create postag layer, with the given tagset");
        options.addOption("l", "lemma", false, "Create lemma layer");
        return options;
    }

    private static void usage(Options options) {
        new HelpFormatter().printHelp(String.format("%s [OPTION]... CONLL TCF", PROGRAM_NAME), options);
        System.exit(1);
    }


}
