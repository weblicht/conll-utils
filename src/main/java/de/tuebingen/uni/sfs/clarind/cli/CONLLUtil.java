package de.tuebingen.uni.sfs.clarind.cli;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.tuebingen.uni.sfs.clarind.tools.ReplaceTool;
import de.tuebingen.uni.sfs.clarind.tools.SamplingTool;
import de.tuebingen.uni.sfs.clarind.tools.TCFTool;
import de.tuebingen.uni.sfs.clarind.tools.Tool;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CONLLUtil {
    private static final List<Tool> TOOLS = ImmutableList.of(new SamplingTool(CONLLUtil.class.getSimpleName()),
            new TCFTool(CONLLUtil.class.getSimpleName()), new ReplaceTool(CONLLUtil.class.getSimpleName()));
    private static final Map<String, Tool> TOOLS_MAP = createToolsMap(TOOLS);

    private static Map<String, Tool> createToolsMap(List<Tool> tools) {
        ImmutableMap.Builder<String, Tool> builder = new ImmutableMap.Builder<>();
        for (Tool tool : tools)
            builder.put(tool.name(), tool);

        return builder.build();
    }

    public static void main(String[] args) {
        if (args.length == 0)
            usage();

        Tool tool = TOOLS_MAP.get(args[0]);
        if (tool == null) {
            System.err.println(String.format("Unknown tool: %s", args[0]));
            System.exit(1);
        }

        try {
            tool.run(Arrays.asList(args).subList(1, args.length));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void usage() {
        System.err.println(String.format("%s TOOL\n", CONLLUtil.class.getSimpleName()));
        System.err.println("Available tools:\n");

        for (String tool : TOOLS_MAP.keySet())
            System.err.println(String.format("\t%s", tool));

        System.err.println("\nUse 'CONLLUtil TOOL' without further arguments to get usage information about a tool");

        System.exit(1);
    }
}
