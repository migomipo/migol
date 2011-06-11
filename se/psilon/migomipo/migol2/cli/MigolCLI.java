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
package se.psilon.migomipo.migol2.cli;

import se.psilon.migomipo.migol2.*;
import se.psilon.migomipo.migol2.parse.*;
import java.io.*;
import java.util.regex.*;
import se.psilon.migomipo.migol2.io.IOManager;
import se.psilon.migomipo.migol2.io.IOUtilities;

public class MigolCLI {

    private static final String VERSION = "11.1.0";
    private static final String VERSIONINFO =
            "MigoMipo Migol 11 interpreter version " + VERSION + "\n"
            + "\u00A9 2009-2011 John Eriksson\n"
            + "Use the flag \"--help\" to list flags";
    private static final String HELP =
            "Flags: \n"
            + "-m size         Sets the number of memory cells\n"
            + "                The number of memory cells are written as an integer\n"
            + "                Suffixes \"k\" and \"m\" are supported\n"
            + "--version       Prints version info\n"
            + "--help          Prints this list of command flags \n";

    public static void main(String[] args) {

        int mem = 1024 * 1024;
        Reader reader = null;
        try {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith("-")) {
                    if (arg.equals("--version")) {
                        System.out.println(VERSIONINFO);
                        return;
                    } else if (arg.equals("--help")) {
                        System.out.println(HELP);
                        return;
                    } else if (arg.equals("-m")) {
                        i++;
                        mem = parseSize(args[i]);
                    }
                } else {
                    if (reader != null) {
                        throw new IllegalArgumentException();
                    }
                    reader = new FileReader(arg);
                }

            }
            if (reader != null) {
                interpret(reader, mem);
            } else {
                System.out.println(VERSIONINFO);
            }
        } catch (RuntimeException ex) {
            System.err.println(HELP);
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex.getMessage());
            System.exit(1);
        }



    }

    private static void interpret(Reader read, int memsize) throws IOException {
        try {
            MigolParsedProgram prog = MigolParser.parse(read);
            MigolExecutionSession session = new MigolExecutionSession(memsize);
            IOManager io = new IOManager();
            IOUtilities.addStdIOFunctions(session, io);
            session.executeProgram(prog);
            io.close();
        } catch (MigolParsingException ex) {
            System.err.println("Parsing error: " + ex.getMessage());
            System.exit(1);
        } catch (MigolExecutionException ex) {
            System.err.println("Execution error: " + ex.getMessage());
            System.exit(1);
        }

    }

    private static int parseSize(String arg) {
        
        Pattern p = Pattern.compile("(\\d+)([mk]?)");
        Matcher m = p.matcher(arg);
        m.matches();
        int modifier = 1;
        if (m.group(2).equals("m")) {
            modifier = 1000000;
        } else if (m.group(2).equals("k")) {
            modifier = 1000;
        }


        return Integer.parseInt(m.group(1)) * modifier;
    }
}
