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
package se.migomipo.migol2.parse;

import java.io.*;
import java.util.List;
import java.util.LinkedList;
import se.migomipo.migol2.*;
import static se.migomipo.migol2.StandardBinaryOperation.*;
import static se.migomipo.migol2.StandardConditionals.*;
import se.migomipo.migol2.execute.MigolExecutionException;
import se.migomipo.migol2.execute.MigolExecutionSession;

/**
 * Contains methods which parses Migol text strings into parsed program, which
 * then can be executed.
 *
 * @see se.migomipo.migol2.MigolParsedProgram
 * @author John Eriksson
 */
public class MigolParser {

    private transient LineNumberReader code;
    private transient List<MigolStatement> statements;
    private transient int strpos;
    private transient String cLine;

    private MigolParser() {
        // Nothing here.
    }

    /**
     * Reads data from a {@link java.io.File} and parses it.
     * @param file  The file object to be read.
     * @return  The parsed program.
     * @throws java.io.IOException  if an I/O error occurs while reading from
     * the file.
     * @throws se.migomipo.migol2.parse.MigolParsingException
     * if the parser encounters a syntax error.
     */
    public static MigolParsedProgram parseFile(File file)
            throws IOException, MigolParsingException {
        return new MigolParser().parseProgram(new FileReader(file));
    }

    /**
     * Reads data from {@link java.io.FileDescriptor} and parses it.
     * @param fd  The file descriptor object to be read.
     * @return  The parsed program.
     * @throws java.io.IOException  if an I/O error occurs while reading from
     * the file.
     * @throws se.migomipo.migol2.parse.MigolParsingException
     * if the parser encounters a syntax error.
     */
    public static MigolParsedProgram parseFile(FileDescriptor fd)
            throws IOException, MigolParsingException {
        return new MigolParser().parseProgram(new FileReader(fd));
    }

    /**
     * Reads data from a file and parses it.
     * @param filename  The file name for the file to be read.
     * @return  The parsed program.
     * @throws java.io.IOException  if an I/O error occurs while reading from
     * the file.
     * @throws se.migomipo.migol2.parse.MigolParsingException
     * if the parser encounters a syntax error.
     */
    public static MigolParsedProgram parseFile(String filename)
            throws IOException, MigolParsingException {
        return new MigolParser().parseProgram(new FileReader(new File(filename)));
    }

    /**
     * Reads data from an {@link java.io.InputStream} object and parses it.
     * @param stream    The {@link java.io.InputStream} object to be read.
     * @return  The parsed program.
     * @throws java.io.IOException if an I/O error occurs while reading from
     * the input stream.
     * @throws se.migomipo.migol2.parse.MigolParsingException
     * if the parser encounters a syntax error.
     */
    public static MigolParsedProgram parse(InputStream stream)
            throws IOException, MigolParsingException {
        return new MigolParser().parseProgram(new InputStreamReader(stream));
    }

    /**
     * Reads data from an {@link java.io.Reader} object and parses it.
     * @param reader    The {@link java.io.Reader} object to be read.
     * @return  The parsed program.
     * @throws java.io.IOException if an I/O error occurs while reading from
     * the Reader.
     * @throws se.migomipo.migol2.parse.MigolParsingException
     * if the parser encounters a syntax error.
     */
    public static MigolParsedProgram parse(Reader reader)
            throws IOException, MigolParsingException {
        return new MigolParser().parseProgram(reader);

    }

    private MigolParsedProgram parseProgram(Reader reader) throws IOException,
            MigolParsingException {
        code = new LineNumberReader(reader);
        statements = new LinkedList<MigolStatement>();
        String line = code.readLine(); // Process first line.
        if (line == null) {
            // If the first line is null, we have the null program.
            return new MigolParsedProgram(new MigolStatement[]{
                        new MigolStatement() {

                            public void executeStatement(MigolExecutionSession session) throws MigolExecutionException {
                                char[] mess = "Null programs are not real quines".toCharArray();
                                for (char c : mess) {
                                    try {
                                        session.getIOCallback().outputChar(c);
                                    } catch (IOException e) {
                                    }
                                }
                            }

                            public String toMigolSyntax() {
                                return "-NULL PROGRAM-";
                            }
                        }
                    }); // Null program are no longer quines
        } else if (!isShebang(line)) {
            // If the first line is not the shebang line, parse it as usual
            parseLine(line);
        } //...then process the rest of the lines.
        while ((line = code.readLine()) != null) {
            parseLine(line);
        }
        MigolStatement[] resultstatements = statements.toArray(
                new MigolStatement[0]); // Convert statement list to array.
        cleanUp();
        return new MigolParsedProgram(resultstatements);

    }

    private void cleanUp() {
        code = null;
        cLine = null;
        statements = null;
    }

    private boolean endOfLine() {
        return strpos >= cLine.length();
    }

    /**
     * Checks if a character represents a number.
     * @param c The character to be controlled.
     * @return  <b>true</b> if the character is a number, <b>false</b> otherwise.
     */
    private boolean isNum(char c) {
        return c >= '0' && c <= '9';
    }

    private void parseLine(String line) throws MigolParsingException {
        cLine = line;
        strpos = 0;
        while (!endOfLine()) {
            skipSpacesAndComments();
            if (endOfLine()) {
                break;
            }
            statements.add(parseStatement());
            skipSpacesAndComments();
            if (endOfLine()) {
                break;
            } else if (cLine.charAt(strpos) == ',') {
                strpos++; // , signifies that a new statement will follow
            } else {
                throw new MigolParsingException("Unexpected character at " +
                        "line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos);
            }
        }
    }

    private MigolStatement parseStatement() throws MigolParsingException {
        try {
            if (cLine.charAt(strpos) == '_') {
                strpos++;
                return NopStatement.__INSTANCE__;
            }
            MigolValue val = parseValue();
            char c = cLine.charAt(strpos);
            if (c == '<') {
                // Assignment statements!
                List<AssignmentOperation> ops = new LinkedList<AssignmentOperation>();
                while (!endOfLine()) {
                    c = cLine.charAt(strpos);
                    if (c == '<') {
                        ops.add(parseOperation());
                    } else if (c == '?') {
                        // A conditional operator has been found.
                        // No more assignment operations can occur after the conditional operator
                        return new ConditionalStatement(new AssignmentStatement(val, ops.toArray(new AssignmentOperation[0])), parseConditional());
                    } else {
                        break; // No more assignment operations
                    }
                }
                return new AssignmentStatement(val, ops.toArray(new AssignmentOperation[0]));

            } else if (c == '>') {
                // Output statement
                strpos++;
                if (endOfLine()) {
                    return new OutputStatement(val, 1);
                }
                int mode = 1;
                c = cLine.charAt(strpos);
                if (c == '-') {
                    strpos++;
                    mode = 2;
                }
                MigolStatement curr = new OutputStatement(val, mode);
                if (!endOfLine()) {
                    c = cLine.charAt(strpos);
                    if (c == '?') {
                        curr = new ConditionalStatement(curr, parseConditional());
                    }
                }
                return curr;

            } else {
                throw new MigolParsingException("Incorrect statement at line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos);
            }


        } catch (StringIndexOutOfBoundsException ex) {
            throw new MigolParsingException("Unexpected end of line at " +
                    "line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos);
        }
    }

    private MigolValue parseValue() throws MigolParsingException {
        int defers = 0; // The number of defers.

        try {
            char c;
            // Count defer values
            while (true) { // Count the ['s
                c = cLine.charAt(strpos);
                if (c == '[') {
                    strpos++; // Next character, please!
                    defers++;
                } else {
                    break;
                }
            }


            if (c == '@') {
                if (defers == 0) {
                    throw new MigolParsingException("Incorrect value " +
                            "at line " + code.getLineNumber(), cLine,
                            code.getLineNumber(), strpos);
                }
                strpos++;
                if (checkRightBrackets() != defers) {
                    throw new MigolParsingException("Incorrect value " +
                            "at line " + code.getLineNumber(), cLine,
                            code.getLineNumber(), strpos);
                }
                return new InputBufferValue(defers);
            } else if (c == '#') {
                strpos++;
                if (checkRightBrackets() != defers) {
                    throw new MigolParsingException("Incorrect value " +
                            "at line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos);
                }
                return new ProgramPointerValue(defers);
            } else if (c == '\'') {
                strpos++;
                int val = (int) cLine.charAt(strpos++);
                if (checkRightBrackets() != defers) {
                    throw new MigolParsingException("Incorrect value " +
                            "at line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos);
                }
                return new IntegerValue(val, defers);

            } else if (isNum(c) || c == '-') {

                int val = parseIntegerValue();
                if (checkRightBrackets() != defers) {
                    throw new MigolParsingException("Incorrect value " +
                            "at line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos);
                }
                return new IntegerValue(val, defers);
            } else {
                throw new MigolParsingException("Unknown value type at " +
                        "line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos);
            }

        } catch (StringIndexOutOfBoundsException ex) {
            throw new MigolParsingException("Unexpected end of line at " +
                    "line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos);
        }

    }

    private AssignmentOperation parseOperation()
            throws MigolParsingException {

        if (cLine.charAt(strpos) != '<') { // This method should never be called if this is false
            throw new IllegalStateException();
        }
        strpos++;
        char c = cLine.charAt(strpos);
        if (c == '$') {
            // Relative operation, check for operator
            strpos++;
            c = cLine.charAt(strpos++);
            if (c == '+') {
                return new StandardBinaryOperation(OP_PLUS, parseValue());
            } else if (c == '-') {
                return new StandardBinaryOperation(OP_MINUS, parseValue());
            } else if (c == '*') {
                return new StandardBinaryOperation(OP_TIMES, parseValue());
            } else if (c == '/') {
                return new StandardBinaryOperation(OP_DIVIDE, parseValue());
            } else if (c == '%') {
                return new StandardBinaryOperation(OP_MOD, parseValue());
            } else if (c == '^') {
                return new StandardBinaryOperation(OP_XOR, parseValue());
            } else if (c == '&') {
                return new StandardBinaryOperation(OP_AND, parseValue());
            } else if (c == '|') {
                return new StandardBinaryOperation(OP_OR, parseValue());
            } else if (c == '!') {
                return new StandardBinaryOperation(OP_XOR, new IntegerValue(-1, 0));
            } else if (c == '<') {
                c = cLine.charAt(strpos);
                if (c == '<') {
                    strpos++;
                    c = cLine.charAt(strpos);
                    if (c == '_') {
                        strpos++;
                        return new StandardBinaryOperation(OP_LRO, parseValue());
                    } else {
                        return new StandardBinaryOperation(OP_LSH, parseValue());
                    }
                } else {
                    throw new MigolParsingException("Unknown assignment " +
                            "operator at line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos - 1);
                }
            } else if (c == '>') {
                c = cLine.charAt(strpos);
                if (c == '>') {
                    strpos++;
                    c = cLine.charAt(strpos);
                    if (c == '>') {
                        strpos++;
                        return new StandardBinaryOperation(OP_RSHL, parseValue());
                    } else if (c == '_') {
                        strpos++;
                        return new StandardBinaryOperation(OP_RRO, parseValue());
                    } else {
                        return new StandardBinaryOperation(OP_RSHA, parseValue());
                    }
                } else {
                    throw new MigolParsingException("Unknown assignment " +
                            "operator at line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos - 1);
                // The pointer points to the character after the incorrect character
                }
            } else {
                throw new MigolParsingException("Unknown assignment " +
                        "operator at line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos - 1);
            // The pointer points to the character after the incorrect character
            }
        } else if (c == '@') {
            strpos++;
            return InputAssignmentOperation.getInstance();
        } else {
            return new StandardBinaryOperation(OP_ASSIGN, parseValue());
        }

    }

    private ConditionalOperation parseConditional()
            throws MigolParsingException {

        char c = cLine.charAt(strpos);
        if (c != '?') {
            throw new IllegalStateException();
        }
        strpos++;
        c = cLine.charAt(strpos);

        if (c == '<') {
            strpos++;
            if (cLine.charAt(strpos) == '=') {
                strpos++;
                return new StandardConditionals(COND_LTEQ, parseValue());
            } else if (cLine.charAt(strpos) == '>') {
                strpos++;
                return new StandardConditionals(COND_NEQ, parseValue());
            } else {
                return new StandardConditionals(COND_LT, parseValue());
            }

        } else if (c == '>') {
            strpos++;
            if (cLine.charAt(strpos) == '=') {
                strpos++;
                return new StandardConditionals(COND_GTEQ, parseValue());
            } else {
                return new StandardConditionals(COND_GT, parseValue());
            }

        } else if (c == '=') {
            strpos++;
            return new StandardConditionals(COND_EQ, parseValue());
        } else {
            throw new MigolParsingException("Unknown conditional operator" +
                    " at line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos);
        }
    }

    private int parseIntegerValue() throws MigolParsingException {
        int start = strpos;
        if (cLine.charAt(strpos) == '-') { // A negative integer
            strpos++;
        }
        while (true) {
            char c = cLine.charAt(strpos);
            if (isNum(c)) {
                strpos++;
            } else {
                break;
            }
            if (endOfLine()) {
                break;
            }
        }
        try {
            if (strpos <= start) {
                throw new MigolParsingException("Incorrect value at " +
                        "line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos);
            }
            return Integer.parseInt(cLine.substring(start, strpos), 10);
        } catch (NumberFormatException ex) {
            throw new MigolParsingException("Incorrect value at " +
                    "line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos);
        }
    }

    private int checkRightBrackets() {
        int count = 0;
        while (!endOfLine()) { // Count the ]'s
            char c = cLine.charAt(strpos);
            if (c == ']') {
                strpos++; // Next character, please!
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    private boolean isShebang(String line) {
        return line.startsWith("#!");
    }

    private void skipSpacesAndComments() throws MigolParsingException {
        while (!endOfLine()) {
            char c = cLine.charAt(strpos);
            if (Character.isSpaceChar(c) || c == '\t') {
                strpos++;
            } else if (c == '/') {
                if (cLine.charAt(strpos + 1) == '/') {
                    // Skip to end of line
                    strpos = cLine.length();
                    break;
                } else { // Single slashes are not allowed
                    throw new MigolParsingException("Unexpected / at line " +
                            code.getLineNumber(), cLine, code.getLineNumber(), strpos);
                }
            } else {
                break;
            }
        }
    }
}
