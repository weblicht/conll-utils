package de.tuebingen.uni.sfs.clarind.tools;

import com.google.common.base.Optional;
import de.tuebingen.uni.sfs.clarind.readers.CONLLReader;
import de.tuebingen.uni.sfs.clarind.readers.CONLLToken;
import de.tuebingen.uni.sfs.clarind.readers.CorpusReader;
import de.tuebingen.uni.sfs.clarind.writers.TCFWriter;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.List;

public class TCFTool implements Tool {
    private static final String TOOL_NAME = "tcf";
    private final String mainClass;

    public TCFTool(String mainClass) {
        this.mainClass = mainClass;
    }

    @Override
    public String name() {
        return TOOL_NAME;
    }

    public void run(List<String> args) throws IOException {
        Options options = programOptions();
        CommandLine cmdLine = processArgs(options, args);

        if (cmdLine.getArgList().size() != 2)
            usage(options);

        File conllFile = new File(cmdLine.getArgs()[0]);
        if (!conllFile.canRead())
            throw new IOException(String.format("Cannot open file for reading: %s", conllFile.getName()));

        File tcfFile = new File(cmdLine.getArgs()[1]);
        String language = cmdLine.hasOption('g') ? cmdLine.getOptionValue('g') : "de";
        boolean lemmas = cmdLine.hasOption('l');
        Optional<String> posTagset = Optional.fromNullable(cmdLine.getOptionValue('p'));
        Optional<String> dependencyTagset = Optional.fromNullable(cmdLine.getOptionValue('d'));

        try (CorpusReader corpusReader = new CONLLReader(new BufferedReader(new FileReader(conllFile)));
             TCFWriter tcfWriter = new TCFWriter(new FileWriter(tcfFile), language, lemmas, posTagset, dependencyTagset)) {
            List<CONLLToken> sentence;
            while ((sentence = corpusReader.readSentence()) != null) {
                tcfWriter.writeSentence(sentence);
            }
        }
    }

    private CommandLine processArgs(Options options, List<String> args) {
        Parser parser = new GnuParser();
        try {
            String[] arr = new String[0];
            return parser.parse(options, args.toArray(arr));
        } catch (ParseException e) {
            usage(options);
        }

        return null; // Shouldn't happen.
    }

    private Options programOptions() {
        Options options = new Options();
        options.addOption("g", "language", true, "The two-letter language code (default: de)");
        options.addOption("d", "dependency", true, "Create dependency layer, with the given tagset");
        options.addOption("p", "postag", true, "Create postag layer, with the given tagset");
        options.addOption("l", "lemma", false, "Create lemma layer");
        return options;
    }

    private void usage(Options options) {
        new HelpFormatter().printHelp(String.format("%s %s [OPTION]... CONLL TCF", mainClass, TOOL_NAME), options);
        System.exit(1);
    }


}
