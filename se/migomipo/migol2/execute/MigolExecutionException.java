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
package se.migomipo.migol2.execute;

/**
 * This exception is thrown when an error occurs during the execution of a
 * parsed Migol program.
 * @see se.migomipo.migol2.MigolParsedProgram
 * @author John Eriksson
 */
public class MigolExecutionException extends Exception {

    private int statementpos;

    /**
     * Constructs a {@code MigolExecutionException} object.
     *
     *
     * @param statementpos      The program counter position where
     * the execution error occured.
     */
    public MigolExecutionException(int statementpos) {
        super();
        this.statementpos = statementpos;

    }

    /**
     * Constructs a {@code MigolExecutionException} object with a specified
     * detail message.
     *
     * @param statementpos      The program counter position where
     * the execution error occured.
     * @param mess      The detail message.
     */
    public MigolExecutionException(String mess, int statementpos) {
        super(mess);
        this.statementpos = statementpos;

    }

    /**
     * Constructs a {@code MigolExecutionException} object with a specified
     * detail message and cause.
     *
     * @param statementpos      The program counter position where
     * the execution error occured.
     * @param mess      The detail message.
     * @param cause     The cause.
     *
     */
    public MigolExecutionException(String mess, Throwable cause, int statementpos) {
        super(mess, cause);
        this.statementpos = statementpos;

    }

    /**
     * Returns the position of the statement which caused the execution error to occur.
     * @return  The position statement which caused this exception, as a
     * integer, with a value >= 1.
     */
    public int getStatementpos() {
        return statementpos;
    }
}
