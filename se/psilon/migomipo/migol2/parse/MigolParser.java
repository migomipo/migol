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

    private LineNumberReader code;
    private List<MigolStatement> statements;
    private int strpos;
    private String cLine;
    private char c;
    private boolean endOfLine;
    private Map<String, Integer> constants;
    private List<ProxyValue> proxies = new LinkedList<ProxyValue>();
    private int statementcount = 0;

    private MigolParser(Reader reader) {
        code = new LineNumberReader(reader);
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
        for(ProxyValue proxy : proxies){
            MigolValue val = proxy.getVal();
            proxy.setVal(val.postProcess(constants));
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
        cLine = line;
        strpos = -1;
        do {
            nextChar();
            skipSpacesAndComments();
            if(endOfLine){
                break;
            }
            statementcount++;
            statements.add(parseStatement());           
            skipSpacesAndComments();     
        } while(!endOfLine && c == ',');
        if(!endOfLine){
            
        }        
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
                    throw new MigolParsingException("Unexpected / at line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos);
                }
            } else if(c == '\"'){
                parseConstant();
            } else {
                break;
            }
        }
    }

    private MigolStatement parseStatement() throws MigolParsingException {
        try {
            if (c == '_') {
                nextChar();
                return new NopStatement();
            }
            MigolValue val = parseValue();
            MigolStatement statement = null;
            if (c == '>') {
                nextChar();
                if (c == '-') {
                    nextChar();
                    statement = new ConsoleIOStatement(val, 1);
                } else {
                    statement = new ConsoleIOStatement(val, 0);
                }
            } else {
                // Assignment statements!
                List<AssignmentOperation> ops = new LinkedList<AssignmentOperation>();
                while (c == '<') {
                    ops.add(parseOperation());
                }
                statement = new AssignmentStatement(val, ops.toArray(new AssignmentOperation[0]));

            }
            if (c == '?') {
                int condtype = parseConditionalType();
                MigolValue condvalue = parseValue();
                statement = new ConditionalStatement(statement, condtype, condvalue);
            }
            if (c == ':'){
                nextChar();
                int namestart = strpos;
                String name = parseName();
                addConstant(name, statementcount, namestart);

            }
            checkFollowingChar();
            return statement;

        } catch (StringIndexOutOfBoundsException ex) {
            throw new MigolParsingException("Unexpected end of line at " + "line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos);
        }
    }

    private MigolValue parseValue() throws MigolParsingException {
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
            MigolValue mval;
            if (c == '#') {
                nextChar();
                mval = new IntegerValue(-1, defers);
            } else if (c == '@') {
                nextChar();
                mval = new IntegerValue(-5, defers);
            } else if (c == '\'') {
                nextChar();
                int val = (int) c;
                nextChar();
                mval = new IntegerValue(val, defers);
            } else if (isNum(c) || c == '-') {
                int val = parseIntegerValue();
                mval = new IntegerValue(val, defers);
            } else if (c >= 'A' && c <= 'Z') {
                int strposstart = strpos;
                String name = parseName();
                ProxyValue proxy = new ProxyValue(
                        new ConstantValue(name, defers, cLine,
                        code.getLineNumber(), strposstart));
                proxies.add(proxy);
                mval = proxy;
            } else {
                throw new MigolParsingException("Unknown value type at " + 
                        "line " + code.getLineNumber(), cLine,
                        code.getLineNumber(), strpos);
            }
            int right = checkRightBrackets();
            if (right != defers && right != 0) {
                throw new MigolParsingException("Incorrect value " + "at line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos);
            }
            return mval;

        } catch (StringIndexOutOfBoundsException ex) {
            throw new MigolParsingException("Unexpected end of line at " + "line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos);
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
                return new AssignmentOperation(OP_PLUS, parseValue());
            } else if (opc == '-') {
                return new AssignmentOperation(OP_MINUS, parseValue());
            } else if (opc == '*') {
                return new AssignmentOperation(OP_MUL, parseValue());
            } else if (opc == '/') {
                return new AssignmentOperation(OP_DIVIDE, parseValue());
            } else if (opc == '%') {
                return new AssignmentOperation(OP_MOD, parseValue());
            } else if (opc == '^') {
                return new AssignmentOperation(OP_XOR, parseValue());
            } else if (opc == '&') {
                return new AssignmentOperation(OP_AND, parseValue());
            } else if (opc == '|') {
                return new AssignmentOperation(OP_OR, parseValue());
            } else if (opc == '!') {
                return new AssignmentOperation(OP_XOR, new IntegerValue(-1, 0));
            } else if (opc == '=') {
                return new AssignmentOperation(OP_EQ, parseValue());
            } else if (opc == '<') {
                if (c == '<') {
                    nextChar();
                    if (c == '_') {
                        nextChar();
                        return new AssignmentOperation(OP_LRO, parseValue());
                    } else {
                        return new AssignmentOperation(OP_LSH, parseValue());
                    }
                } else if (c == '=') {
                    nextChar();
                    return new AssignmentOperation(OP_LTEQ, parseValue());
                } else if (c == '>') {
                    nextChar();
                    return new AssignmentOperation(OP_NEQ, parseValue());
                } else {
                    return new AssignmentOperation(OP_LT, parseValue());
                }
            } else if (opc == '>') {
                if (c == '>') {
                    nextChar();
                    if (c == '>') {
                        nextChar();
                        return new AssignmentOperation(OP_RSHL, parseValue());
                    } else if (c == '_') {
                        nextChar();
                        return new AssignmentOperation(OP_RRO, parseValue());
                    } else {
                        return new AssignmentOperation(OP_RSHA, parseValue());
                    }
                } else if (c == '=') {
                    nextChar();
                    return new AssignmentOperation(OP_GTEQ, parseValue());
                } else {
                    return new AssignmentOperation(OP_GT, parseValue());
                }
            }
        } else {
            return new AssignmentOperation(OP_ASSIGN, parseValue());
        }
        throw new MigolParsingException("Unknown assignment " + "operator at line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos - 1);
        // The pointer points to the character after the incorrect character

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
            throw new MigolParsingException("Unknown conditional operator" + " at line " + code.getLineNumber(), cLine, code.getLineNumber(), strpos);
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
                        "Incorrect value at " + "line " + code.getLineNumber(),
                        cLine, code.getLineNumber(), strpos);
            }
            return Integer.parseInt(cLine.substring(start, strpos), 10);
        } catch (NumberFormatException ex) {
            throw new MigolParsingException(
                    "Incorrect value at " + "line " + code.getLineNumber(),
                    cLine, code.getLineNumber(), strpos);
        }
    }

    private String parseName() throws MigolParsingException {
        int start = strpos;
        while (c >= 'A' && c <= 'Z') {
            nextChar();
            if (endOfLine) {
                break;
            }
        }
        if (strpos <= start) {
            throw new MigolParsingException(
                    "Incorrect value at " + "line " + code.getLineNumber(),
                    cLine, code.getLineNumber(), strpos);
        }
        return cLine.substring(start, strpos);


    }
    
    private boolean isBlank(char c){
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
        if(i != null){
            throw new MigolParsingException(
                    "Constant defined multiple times at line " +
                    code.getLineNumber(), cLine, code.getLineNumber(), namestrpos);
        }
    }

    private void parseConstant() throws MigolParsingException {
        assert(c == '"');
        nextChar();
        int startstrpos = strpos;
        String name = parseName();
        if(c != '='){
            throw new MigolParsingException(
                    "Unexpected character at line " + code.getLineNumber(), 
                    cLine, code.getLineNumber(), strpos);
        }
        nextChar();
        int value = parseIntegerValue();
        addConstant(name, value, startstrpos);
        checkFollowingChar();
    }

    private void checkFollowingChar() throws MigolParsingException {
        if(!(endOfLine || isBlank(c) || c == '/' || c == ',')){
            throw new MigolParsingException(
                    "Unexpected character at line " + code.getLineNumber(),
                    cLine, code.getLineNumber(), strpos);
        }
    }
}
