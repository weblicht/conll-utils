package de.tuebingen.uni.sfs.clarind.tools;

import java.io.IOException;
import java.util.List;

public interface Tool {
    /**
     * Return the name of this tool.
     * @return The tool name.
     */
    public String name();

    /**
     * Run the tool with the provided arguments.
     * @param args Tool arguments.
     */
    public void run(List<String> args) throws IOException;
}
