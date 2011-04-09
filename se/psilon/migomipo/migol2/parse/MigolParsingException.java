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

/**
 * Signaled by the parser whenever a syntax error has been found in the parsed
 * text.
 * @see se.psilon.migomipo.migol2.parse.MigolParser
 * @author John Eriksson
 */
public class MigolParsingException extends Exception {
    private final String errorline;
    private final int linenumber;
    private final int errorpos;
    private final String errorpointer;
    
    /**
     * Constructs a {@code MigolParsingException} object.
     * @param message   The error detail message.
     * @param errorline     The code line where the syntax error was found.
     * @param linenumber    The number of the line where the 
     * @param errorpos      The character position where the syntax error was 
     * found.
     */
    public MigolParsingException(String message,String errorline, int linenumber, int errorpos) {        
        super(message + " in line " + linenumber);
        this.errorline = errorline;
        this.linenumber = linenumber;
        this.errorpos = errorpos;
        this.errorpointer = generateErrorPointer();
    }
    /**
     * Constructs a {@code MigolParsingException} object with a specified 
     * cause.
     * @param message   The error detail message.
     * @param cause     The cause.
     * @param errorline     The code line where the syntax error was found.
     * @param linenumber    The number of the line where the 
     * @param errorpos      The character position where the syntax error was 
     * found.
     */
    public MigolParsingException(String message, Throwable cause,String errorline, int linenumber, int errorpos) {
        super(message, cause);
        this.errorline = errorline;
        this.linenumber = linenumber;
        this.errorpos = errorpos;
        this.errorpointer = generateErrorPointer();
    }
    
    /**
     * Returns a String describing the syntax error.
     * @return  The error message and error highlighting.
     */
    @Override
    public String getMessage(){
        return super.getMessage() + "\n" + errorpointer;
    }

    /**
     * Returns a String describing the syntax error.
     * @return  The error message and error highlighting.
     */
    @Override
    public String getLocalizedMessage(){
        return this.getMessage();
    }

    public String getErrorline() {
        return errorline;
    }

    public String getErrorpointer() {
        return errorpointer;
    }

    public int getErrorpos() {
        return errorpos;
    }

    public int getLinenumber() {
        return linenumber;
    }

    private String generateErrorPointer() {
        StringBuffer s = new StringBuffer();
        s.append("\t" + errorline + "\n\t");
        for(int i=0;i<errorpos;i++){
            s.append(' ');
        }
        s.append('^');
        return s.toString();
    }
}
