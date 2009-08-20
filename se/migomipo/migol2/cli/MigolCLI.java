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
import se.migomipo.migol2.execute.MigolExecutionException;
import se.migomipo.migol2.execute.MigolExecutionSession;
import se.migomipo.migol2.parse.MigolParser;
import se.migomipo.migol2.parse.MigolParsingException;

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

    public static void main(String[] args) {
        try {
            boolean ncommand = false;
            String filename = null;
            boolean timing = false;
            for (String s : args) {
                if (!ncommand && s.startsWith("-")) {
                    String command = s.substring(1);
                    if(command.equals("-version")){
                        System.out.println("MigoMipo Migol 09 interpreter version 2.3.0");
                        System.out.println("\u00A9 John Eriksson 2009");
                        return;
                    }
                    else if (command.equals("t")) {
                        timing = true;
                    } else if(command.equals("-")){
                        ncommand = true;
                    }
                } else {
                    filename = s;
                    break;
                }
            }
            if (filename == null) {
                System.err.println("MigoMipo Migol 09 interpreter version 2.2.1");
                System.err.println("Usage: Migol2-0.jar [options] <filename>");
                System.err.println("\u00A9 2009 John Eriksson \n");
                System.err.println("Options : ");
                System.err.println("  -t              Prints time taken for parsing and executing the program");
                System.err.println("  --version       Prints version information and quits");
                return;
            }
            if (timing) {
                long startparsetime = System.currentTimeMillis();
                MigolParsedProgram prog = MigolParser.parseFile(filename);
                long totalparsetime = System.currentTimeMillis() - startparsetime;
                System.out.println("Parse time : " + totalparsetime + " ms");
                System.gc();
                long startexectime = System.currentTimeMillis();
                prog.executeProgram(new MigolExecutionSession());
                long totalexectime = System.currentTimeMillis() - startexectime;
                System.out.println("Execution time : " + totalexectime + " ms");

            } else {
                MigolParsedProgram prog = MigolParser.parseFile(filename);
                System.gc();
                prog.executeProgram(new MigolExecutionSession());
            }
        } catch (MigolParsingException ex) {
            System.err.println("Parsing error : " + ex.getMessage());
        } catch (MigolExecutionException ex) {
            System.err.println("Execution error : " + ex.getMessage());
        } catch (java.io.IOException ex) {
            System.err.println("I/O error : " + ex.getMessage());
        }

    }
}
