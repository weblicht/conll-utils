package de.tuebingen.uni.sfs.clarind.conllutils.util;

import java.io.*;

public class IOUtils {
    public static BufferedReader openArgOrStdin(String[] args, int n) throws IOException {
        if (n < args.length)
            return new BufferedReader(new FileReader(args[n]));

        return new BufferedReader(new InputStreamReader(System.in));
    }

    public static BufferedWriter openArgOrStdout(String[] args, int n) throws IOException {
        if (n < args.length)
            return new BufferedWriter(new FileWriter(args[n]));

        return new BufferedWriter(new PrintWriter(System.out));
    }
}
