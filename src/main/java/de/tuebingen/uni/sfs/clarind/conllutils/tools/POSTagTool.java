package de.tuebingen.uni.sfs.clarind.conllutils.tools;

import com.google.common.collect.ImmutableList;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLReader;
import de.tuebingen.uni.sfs.clarind.conllutils.readers.CONLLToken;
import de.tuebingen.uni.sfs.clarind.conllutils.writers.CONLLWriter;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.List;

public class POSTagTool implements Tool {
    private static final String TOOL_NAME = "postag";
    private final String mainName;

    public POSTagTool(String mainName) {
        this.mainName = mainName;
    }

    @Override
    public String name() {
        return TOOL_NAME;
    }

    @Override
    public void run(List<String> args) throws IOException {
        Options options = programOptions();
        CommandLine cmdLine = parseArguments(options, args);

        if (cmdLine.getArgs().length != 2)
            usage(options);

        if (!cmdLine.hasOption('c') && !cmdLine.hasOption('f'))
            throw new IOException("Nothing to do!");

        if (cmdLine.hasOption('c') && cmdLine.hasOption('f'))
            throw new IOException("Cannot use both coarse-grained and fine-grained tags!");

        File inFile = new File(cmdLine.getArgs()[0]);
        File outFile = new File(cmdLine.getArgs()[1]);


        try (CONLLReader reader = new CONLLReader(new BufferedReader(new FileReader(inFile)));
             CONLLWriter writer = new CONLLWriter(new BufferedWriter(new FileWriter(outFile)))) {
            List<CONLLToken> sentence;
            while ((sentence = reader.readSentence()) != null) {
                ImmutableList.Builder<CONLLToken> builder = ImmutableList.builder();

                for (CONLLToken token : sentence) {
                    if (cmdLine.hasOption('c'))
                        builder.add(new CONLLToken(token.getPosition(), token.getForm(), token.getLemma(),
                                token.getCoursePOSTag(), token.getCoursePOSTag(), token.getFeatures(), token.getHead(),
                                token.getDepRel(), token.getPHead(), token.getPDepRel()));
                    else
                        builder.add(new CONLLToken(token.getPosition(), token.getForm(), token.getLemma(),
                                token.getPosTag(), token.getPosTag(), token.getFeatures(), token.getHead(),
                                token.getDepRel(), token.getPHead(), token.getPDepRel()));
                }

                writer.writeSentence(builder.build());
            }
        }
    }

    private CommandLine parseArguments(Options options, List<String> args) {
        Parser parser = new GnuParser();
        String[] arr = new String[0];
        CommandLine cmdLine = null;
        try {
            cmdLine = parser.parse(options, args.toArray(arr));
        } catch (ParseException e) {
            usage(options);
        }
        return cmdLine;
    }

    private Options programOptions() {
        Options options = new Options();
        options.addOption("c", "coarse", false, "Replace fine-grained postags by coarse-grained");
        options.addOption("f", "fine", false, "Replace coarse-grained postags by fine-grained");
        return options;
    }

    private void usage(Options options) {
        new HelpFormatter().printHelp(String.format("Usage: %s %s [OPTIONS] CONLL CONLL_OUT", mainName, TOOL_NAME), options);
        System.exit(1);
    }
}
