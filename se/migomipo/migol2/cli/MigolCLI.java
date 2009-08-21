/*
 * Copyright (c) 2009 John Eriksson

 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:

 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package se.migomipo.migol2.cli;

import se.migomipo.migol2.*;
import se.migomipo.migol2.execute.*;
import se.migomipo.migol2.parse.*;
import java.io.*;

/**
 * The command line interface for Migol 09 2.3.
 *
 * The program reads a file, parses and executes it.
 * @see se.migomipo.migol2.parse.MigolParser
 * @see se.migomipo.migol2.MigolParsedProgram
 * @see se.migomipo.migol2.execute.MigolExecutionSession
 * @author John Eriksson
 */
public class MigolCLI {

    private static final String VERSION = "2.3.1";
    private static final String VERSIONINFO =
            "MigoMipo Migol 09 interpreter version " + VERSION + "\n" +
            "\u00A9 2009 John Eriksson";
    private static final String USAGEINFO =
            "Usage: java -jar Migol2.jar [flags] [--] [filename]";
    private static final String FLAGSINFO =
            "Flags: \n" +
            "  -e program      Ignore file name, and interpret line after \n" +
            "                  flag (several -e's allowed)";

    public static void main(String[] args) {
        boolean filemode = true;
        boolean flag = true;
        String interpretline = "";

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (flag && arg.startsWith("-")) {
                if (arg.equals("--version")) {
                    System.out.println(VERSIONINFO);
                    return;
                }
                if (arg.equals("--help")) {
                    System.out.println(USAGEINFO);
                    System.out.println(FLAGSINFO);
                    return;
                } else if (arg.equals("--")) {
                    flag = false;
                } else if (arg.equals("-e")) {
                    filemode = false;
                    if (i + 1 < args.length) {
                        i++;
                        interpretline = interpretline.concat(args[i] + "\n");
                    }
                } else {
                    System.err.println("Error: Unknown flag \"" + arg + "\"");
                    System.exit(1);
                }
            } else if (filemode) {
                try {
                    interpret(new FileReader(arg));
                } catch (FileNotFoundException ex) {
                    System.err.println("File not found");
                    System.exit(1);
                }
                return;
            } else {
            } // Ignore if -e set and not a flag
        }

        if (interpretline.length() == 0) {
            System.err.println(USAGEINFO);
            System.exit(1);
        } else {
            interpret(new StringReader(interpretline));
        }
    }

    private static void interpret(Reader read) {
        try {
            MigolParsedProgram prog = MigolParser.parse(read);
            MigolExecutionSession session = new MigolExecutionSession();
            prog.executeProgram(session);

        } catch (IOException ex) {
            System.err.println("I/O error: " + ex.getMessage());
            System.exit(1);
        } catch (MigolParsingException ex) {
            System.err.println("Parsing error: " + ex.getMessage());
            System.exit(1);
        } catch (MigolExecutionException ex) {
            System.err.println("Execution error: " + ex.getMessage());
            System.exit(1);
        }

    }
}
