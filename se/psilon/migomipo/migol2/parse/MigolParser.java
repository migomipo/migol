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
package se.psilon.migomipo.migol2.parse;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.*;
import se.psilon.migomipo.migol2.*;
import static se.psilon.migomipo.migol2.AssignmentOperation.*;
import static se.psilon.migomipo.migol2.ConditionalStatement.*;

/**
 * Contains methods which parses Migol text strings into parsed program, which
 * then can be executed.
 *
 * @see se.psilon.migomipo.migol2.MigolParsedProgram
 * @author John Eriksson
 */
public class MigolParser {

    private BufferedReader code;
    private List<MigolStatement> statements;
    private int strpos;
    private String cLine;
    private char c;
    private boolean endOfLine;
    private Map<String, Integer> constants;
    private Map<String, LabelValue> labelValues = new HashMap<String, LabelValue>();
    private List<SavedLabel> labels = new LinkedList<SavedLabel>();
    private int statementcount = 0;
    private int linenum = 0;

    private class SavedLabel {

        private String cLine;
        private int linenum, strpos;
        private LabelValue value;

        public SavedLabel(LabelValue value, String cLine, int linenum, int strpos) {
            this.cLine = cLine;
            this.linenum = linenum;
            this.strpos = strpos;
            this.value = value;
        }

        private void resolve(Map<String, Integer> constants) throws MigolParsingException {
            try {
                value.resolve(constants);
            } catch (NullPointerException ex) {
                throw new MigolParsingException("Undefined label", cLine, linenum, strpos);
            }
        }
    }

    private MigolParser(Reader reader) {
        code = new BufferedReader(reader);
        constants = new HashMap<String, Integer>();
    }

    public static MigolParsedProgram parseString(String str)
            throws MigolParsingException {
        try {
            return new MigolParser(new StringReader(str)).parseProgram();
        } catch (IOException ex) {
            throw new IllegalStateException(ex); // StringReader should never throw an exception
        }
    }

    /**
     * Reads data from a {@link java.io.File} and parses it.
     * @param file  The file object to be read.
     * @return  The parsed program.
     * @throws java.io.IOException  if an I/O error occurs while reading from
     * the file.
     * @throws se.psilon.migomipo.migol2.parse.MigolParsingException
     * if the parser encounters a syntax error.
     */
    public static MigolParsedProgram parseFile(File file)
            throws IOException, MigolParsingException {
        return new MigolParser(new FileReader(file)).parseProgram();
    }

    /**
     * Reads data from {@link java.io.FileDescriptor} and parses it.
     * @param fd  The file descriptor object to be read.
     * @return  The parsed program.
     * @throws java.io.IOException  if an I/O error occurs while reading from
     * the file.
     * @throws se.psilon.migomipo.migol2.parse.MigolParsingException
     * if the parser encounters a syntax error.
     */
    public static MigolParsedProgram parseFile(FileDescriptor fd)
            throws IOException, MigolParsingException {
        return new MigolParser(new FileReader(fd)).parseProgram();
    }

    /**
     * Reads data from a file and parses it.
     * @param filename  The file name for the file to be read.
     * @return  The parsed program.
     * @throws java.io.IOException  if an I/O error occurs while reading from
     * the file.
     * @throws se.psilon.migomipo.migol2.parse.MigolParsingException
     * if the parser encounters a syntax error.
     */
    public static MigolParsedProgram parseFile(String filename)
            throws IOException, MigolParsingException {
        return new MigolParser(new FileReader(new File(filename))).parseProgram();
    }

    /**
     * Reads data from an {@link java.io.InputStream} object and parses it.
     * @param stream    The {@link java.io.InputStream} object to be read.
     * @return  The parsed program.
     * @throws java.io.IOException if an I/O error occurs while reading from
     * the input stream.
     * @throws se.psilon.migomipo.migol2.parse.MigolParsingException
     * if the parser encounters a syntax error.
     */
    public static MigolParsedProgram parse(InputStream stream)
            throws IOException, MigolParsingException {
        return new MigolParser(new InputStreamReader(stream)).parseProgram();
    }

    /**
     * Reads data from an {@link java.io.InputStream} object and parses it.
     * @param stream    The {@link java.io.InputStream} object to be read.
     * @param charsetname
     *         The name of a supported
     *         {@link java.nio.charset.Charset </code>charset<code>}
     * @return  The parsed program.
     * @throws java.io.IOException if an I/O error occurs while reading from
     * the input stream.
     * @throws se.psilon.migomipo.migol2.parse.MigolParsingException
     * if the parser encounters a syntax error.
     */
    public static MigolParsedProgram parse(InputStream stream, String charsetname)
            throws IOException, MigolParsingException {
        return new MigolParser(new InputStreamReader(stream, charsetname)).parseProgram();
    }

    /**
     * Reads data from an {@link java.io.InputStream} object and parses it.
     * @param stream    The {@link java.io.InputStream} object to be read.
     * @param  cs       A charset
     * @return  The parsed program.
     * @throws java.io.IOException if an I/O error occurs while reading from
     * the input stream.
     * @throws se.psilon.migomipo.migol2.parse.MigolParsingException
     * if the parser encounters a syntax error.
     */
    public static MigolParsedProgram parse(InputStream stream, Charset charset)
            throws IOException, MigolParsingException {
        return new MigolParser(new InputStreamReader(stream, charset)).parseProgram();
    }

    /**
     * Reads data from an {@link java.io.InputStream} object and parses it.
     * @param stream    The {@link java.io.InputStream} object to be read.
     * @param  dec      A charset decoder
     * @return  The parsed program.
     * @throws java.io.IOException if an I/O error occurs while reading from
     * the input stream.
     * @throws se.psilon.migomipo.migol2.parse.MigolParsingException
     * if the parser encounters a syntax error.
     */
    public static MigolParsedProgram parse(InputStream stream, CharsetDecoder charset)
            throws IOException, MigolParsingException {
        return new MigolParser(new InputStreamReader(stream, charset)).parseProgram();
    }

    /**
     * Reads data from an {@link java.io.Reader} object and parses it.
     * @param reader    The {@link java.io.Reader} object to be read.
     * @return  The parsed program.
     * @throws java.io.IOException if an I/O error occurs while reading from
     * the Reader.
     * @throws se.psilon.migomipo.migol2.parse.MigolParsingException
     * if the parser encounters a syntax error.
     */
    public static MigolParsedProgram parse(Reader reader)
            throws IOException, MigolParsingException {
        return new MigolParser(reader).parseProgram();
    }

    private MigolParsedProgram parseProgram() throws IOException,
            MigolParsingException {

        statements = new LinkedList<MigolStatement>();
        String line = code.readLine(); // Process first line.
        if (line == null) {
            // If the first line is null, we have the null program.
            return new MigolParsedProgram(new MigolStatement[0]);
        } else if (!isShebang(line)) {
            // IparseLinef the first line is not the shebang line, parse it as usual
            parseLine(line);
        } //...then process the rest of the lines.
        while ((line = code.readLine()) != null) {
            parseLine(line);
        }
        for (SavedLabel label : labels) {
            label.resolve(constants);
        }
        return new MigolParsedProgram(statements);

    }

    /**
     * Checks if a character represents a number.
     * @param c The character to be controlled.
     * @return  <b>true</b> if the character is a number, <b>false</b> otherwise.
     */
    private boolean isNum(char c) {
        return c >= '0' && c <= '9';
    }

    private void nextChar() {
        strpos++;
        endOfLine = strpos >= cLine.length();
        if (endOfLine) {
            c = 0;
        } else {
            c = cLine.charAt(strpos);
        }

    }

    private void parseLine(String line) throws MigolParsingException {
        linenum++;
        cLine = line;
        strpos = -1;
        do {
            nextChar();
            skipSpacesAndComments();
            if (endOfLine) {
                break;
            }
            statementcount++;
            statements.add(parseStatement());
            skipSpacesAndComments();
        } while (!endOfLine && c == ',');

    }

    private void skipSpacesAndComments() throws MigolParsingException {
        while (!endOfLine) {
            if (isBlank(c)) {
                nextChar();
            } else if (c == '/') {
                nextChar();
                if (c == '/') {
                    // Skip to end of line
                    strpos = cLine.length();
                    endOfLine = true;
                    break;
                } else { // Single slashes are not allowed
                    throw new MigolParsingException("Unexpected /", cLine,
                            linenum, strpos);
                }
            } else {
                break;
            }
        }
    }

    private MigolStatement parseStatement() throws MigolParsingException {
        try {

            MigolReference val;
            MigolStatement statement = null;
            if (c == '_') {
                nextChar();
                statement = NopStatement.getInstance();
            } else {
                int startpos = strpos;
                val = parseValue();
                if (c == '>') {
                    MigolValue val2;
                    try {
                        val2 = (MigolValue) val;
                    } catch (ClassCastException ex) {
                        throw new MigolParsingException("Write-only value used in "
                                + "reading position", cLine, linenum, startpos);
                    }

                    nextChar();
                    if (c == '-') {
                        nextChar();
                        statement = new ConsoleIOStatement(val2, ConsoleIOStatement.INT);
                    } else {
                        statement = new ConsoleIOStatement(val2, ConsoleIOStatement.ASCII);
                    }
                } else if (c == '<') {
                    // Assignment statements!
                    List<AssignmentOperation> ops = new LinkedList<AssignmentOperation>();
                    while (c == '<') {
                        ops.add(parseOperation());
                    }
                    statement = new AssignmentStatement(val, ops.toArray(new AssignmentOperation[0]));

                } else {
                    throw new MigolParsingException("Incorrect statement", cLine, linenum, strpos);
                }
            }
            if (c == '?') {
                int condtype = parseConditionalType();
                MigolValue condvalue = parseReadValue();
                statement = new ConditionalStatement(statement, condtype, condvalue);
            }
            if (c == ':') {
                nextChar();
                int namestart = strpos;
                String name = parseName();
                addConstant(name, statementcount, namestart);

            }
            checkFollowingChar();
            return statement;

        } catch (StringIndexOutOfBoundsException ex) {
            throw new MigolParsingException("Unexpected end of line", cLine, linenum, strpos);
        }
    }

    private MigolReference parseValue() throws MigolParsingException {
        int defers = 0; // The number of defers.

        try {
            // Count defer values
            while (true) { // Count the ['s

                if (c == '[') {
                    nextChar();
                    defers++;
                } else {
                    break;
                }
            }
            MigolReference mval;
            if (c == '#') {
                nextChar();
                if (c == '!') {
                    nextChar();
                    mval = BranchLeaveHandlerReference.getInstance();
                } else {
                    mval = BranchReference.getInstance();
                }

            } else if (c == '@') {
                nextChar();
                mval = ConsoleInputReference.getInstance();
            } else if (c == '!') {
                nextChar();
                if (c == '#') {
                    nextChar();
                    mval = InterruptHandlerReference.getInstance();
                } else {
                    mval = ExecReference.getInstance();
                }
            } else if (c == '*') {
                nextChar();
                if (c == '#') {
                    nextChar();
                    mval = InterruptReturnAddressReference.getInstance();
                } else if (c == '!') {
                    nextChar();
                    mval = InterruptResultReference.getInstance();
                } else {
                    throw new MigolParsingException("Unknown value type", cLine, linenum, strpos);
                }
            } else if (c == '\\') {
                nextChar();
                mval = InterruptWaitReference.getInstance();
            } else if (c == '\'') {
                nextChar();
                int val = (int) c;
                nextChar();
                mval = IntegerValue.getInstance(val);
            } else if (isNum(c) || c == '-') {
                int val = parseIntegerValue();
                mval = IntegerValue.getInstance(val);
            } else if (isValidName(c)) {
                int labelpos = strpos;                
                String name = parseName();
                LabelValue l = labelValues.get(name);
                if(l == null){
                    l = new LabelValue(name);
                    labelValues.put(name, l);
                    labels.add(new SavedLabel(l, cLine, linenum, labelpos));
                    // Stores the first usage of each label
                }
                mval = l;
            } else {
                throw new MigolParsingException("Unknown value type", cLine, linenum, strpos);
            }
            int right = checkRightBrackets();
            if (right != defers && right != 0) {
                throw new MigolParsingException("Incorrect value", cLine, linenum, strpos);
            }
            if (defers > 0) {
                mval = DeferValue.getInstance(mval, defers);
            }
            return mval;

        } catch (StringIndexOutOfBoundsException ex) {
            throw new MigolParsingException("Unexpected end of line", cLine, linenum, strpos);
        }

    }

    private AssignmentOperation parseOperation()
            throws MigolParsingException {

        assert (c == '<');  // This method should never be called if this is false
        nextChar();

        if (c == '$') {
            // Relative operation, check for operator
            nextChar();
            char opc = c;
            nextChar();
            if (opc == '+') {
                return AssignmentOperation.getInstance(OP_PLUS, parseReadValue());
            } else if (opc == '-') {
                return AssignmentOperation.getInstance(OP_MINUS, parseReadValue());
            } else if (opc == '*') {
                return AssignmentOperation.getInstance(OP_MUL, parseReadValue());
            } else if (opc == '/') {
                return AssignmentOperation.getInstance(OP_DIVIDE, parseReadValue());
            } else if (opc == '%') {
                return AssignmentOperation.getInstance(OP_MOD, parseReadValue());
            } else if (opc == '^') {
                return AssignmentOperation.getInstance(OP_XOR, parseReadValue());
            } else if (opc == '&') {
                return AssignmentOperation.getInstance(OP_AND, parseReadValue());
            } else if (opc == '|') {
                return AssignmentOperation.getInstance(OP_OR, parseReadValue());
            } else if (opc == '=') {
                return AssignmentOperation.getInstance(OP_EQ, parseReadValue());
            } else if (opc == '<') {
                if (c == '<') {
                    nextChar();
                    if (c == '_') {
                        nextChar();
                        return AssignmentOperation.getInstance(OP_LRO, parseReadValue());
                    } else {
                        return AssignmentOperation.getInstance(OP_LSH, parseReadValue());
                    }
                } else if (c == '=') {
                    nextChar();
                    return AssignmentOperation.getInstance(OP_LTEQ, parseReadValue());
                } else if (c == '>') {
                    nextChar();
                    return AssignmentOperation.getInstance(OP_NEQ, parseReadValue());
                } else {
                    return AssignmentOperation.getInstance(OP_LT, parseReadValue());
                }
            } else if (opc == '>') {
                if (c == '>') {
                    nextChar();
                    if (c == '>') {
                        nextChar();
                        return AssignmentOperation.getInstance(OP_RSHL, parseReadValue());
                    } else if (c == '_') {
                        nextChar();
                        return AssignmentOperation.getInstance(OP_RRO, parseReadValue());
                    } else {
                        return AssignmentOperation.getInstance(OP_RSHA, parseReadValue());
                    }
                } else if (c == '=') {
                    nextChar();
                    return AssignmentOperation.getInstance(OP_GTEQ, parseReadValue());
                } else {
                    return AssignmentOperation.getInstance(OP_GT, parseReadValue());
                }
            } else if(opc == '!'){
                throw new MigolParsingException(
                        "Obsolete bitwise not operator (replace <$! with <$^-1)"
                        , cLine, linenum, strpos - 1);
            }
        } else {
            return AssignmentOperation.getInstance(OP_ASSIGN, parseReadValue());
        }
        throw new MigolParsingException("Unknown assignment operator", cLine,
                linenum, strpos - 1);
        // The pointer points to the character after the incorrect character

    }

    private MigolValue parseReadValue() throws MigolParsingException {
        int startpos = strpos;
        try {
            return (MigolValue) parseValue();
        } catch (ClassCastException ex) {
            throw new MigolParsingException("Write-only value used in "
                    + "reading position", cLine, linenum, startpos);
        }
    }

    private int parseConditionalType()
            throws MigolParsingException {

        assert (c == '?');
        nextChar();

        if (c == '<') {
            nextChar();
            if (c == '=') {
                nextChar();
                return COND_LTEQ;
            } else if (c == '>') {
                nextChar();
                return COND_NEQ;
            } else {
                return COND_LT;
            }

        } else if (c == '>') {
            nextChar();
            if (c == '=') {
                nextChar();
                return COND_GTEQ;
            } else {
                return COND_GT;
            }

        } else if (c == '=') {
            nextChar();
            return COND_EQ;
        } else {
            throw new MigolParsingException("Unknown conditional operator",
                    cLine, linenum, strpos);
        }
    }

    private int parseIntegerValue() throws MigolParsingException {
        int start = strpos;
        if (c == '-') { // A negative integer
            nextChar();
        }
        while (true) {
            if (isNum(c)) {
                nextChar();
            } else {
                break;
            }
            if (endOfLine) {
                break;
            }
        }
        try {
            if (strpos <= start) {
                throw new MigolParsingException(
                        "Incorrect value", cLine, linenum, strpos);
            }
            return Integer.parseInt(cLine.substring(start, strpos), 10);
        } catch (NumberFormatException ex) {
            throw new MigolParsingException(
                    "Incorrect value", cLine, linenum, strpos);
        }
    }
    
    
    private boolean isValidName(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private String parseName() throws MigolParsingException {
        int start = strpos;
        if(isValidName(c)){
            nextChar();
            while (!endOfLine && (isValidName(c) || c == '_')) {
                nextChar();
            }
        } else {
            throw new MigolParsingException(
                    "Incorrect name", cLine, linenum, strpos);
        }
        return cLine.substring(start, strpos);
    }

    private boolean isBlank(char c) {
        return Character.isSpaceChar(c) || c == '\t';
    }

    private int checkRightBrackets() {
        int count = 0;
        while (!endOfLine) { // Count the ]'s
            if (c == ']') {
                nextChar();
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

    private void addConstant(String name, int statementcount, int namestrpos) throws MigolParsingException {
        Integer i = constants.put(name, statementcount);
        if (i != null) {
            throw new MigolParsingException(
                    "Constant defined multiple times", cLine,
                    linenum, namestrpos);
        }
    }


    private void checkFollowingChar() throws MigolParsingException {
        if (!(endOfLine || isBlank(c) || c == '/' || c == ',')) {
            throw new MigolParsingException(
                    "Unexpected character",
                    cLine, linenum, strpos);
        }
    }
}
