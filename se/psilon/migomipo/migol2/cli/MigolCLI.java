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
import se.psilon.migomipo.migol2.io.FileOperationManager;
import se.psilon.migomipo.migol2.io.IOManager;
import se.psilon.migomipo.migol2.io.IOUtilities;
import se.psilon.migomipo.migol2.io.SocketManager;


public class MigolCLI {

    private static final String VERSION = "11.0.7";
    private static final String VERSIONINFO =
            "MigoMipo Migol 11 interpreter version " + VERSION + "\n" +
            "\u00A9 2009-2011 John Eriksson";

    
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
                else {
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
            System.err.println(VERSIONINFO);
            System.exit(1);
        } else {
            interpret(new StringReader(interpretline));
        }
    }

    private static void interpret(Reader read) {
        try {
            MigolParsedProgram prog = MigolParser.parse(read);
            MigolExecutionSession session = new MigolExecutionSession();  
            IOManager io = new IOManager();
            IOUtilities.addStdIOFunctions(session, io);
            session.executeProgram(prog);
            io.close();
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
