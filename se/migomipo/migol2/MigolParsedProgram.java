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
package se.migomipo.migol2;

import java.io.Serializable;
import java.util.Arrays;
import se.migomipo.migol2.execute.*;

/**
 * A pre-parsed Migol program that can be executed on a {@link MigolExecutionSession}.
 *
 * <code>MigolParsedProgram</code> objects are returned from the Migol code parser.
 * It executes the program by looping through an array of {@link MigolStatement}
 * objects and executing them.
 *
 * Instances of this class are not safe for use by multiple concurrent threads.
 *
 * @see MigolExecutionSession
 * @see MigolStatement
 * @author John Eriksson
 */
public class MigolParsedProgram implements Serializable {

    /**
     * The rendered {@link MigolStatement} objects to be executed.
     */
    private final MigolStatement[] statements;

    /**
     * Returns the number of statements in the program.
     * @return The number of statements in the program as an integer.
     */
    public int size() {
        return statements.length;
    }

    /**
     * Executes the program with the given session.
     *
     * The session will not be reset. If an old session object is used, the old
     * memory content will still be preserved when execution starts.
     * @param session   The session which will be manipulated in this program
     * execution.
     * @throws se.migomipo.migol2.execute.MigolExecutionException
     * If an error occurs during execution.
     */
    public void executeProgram(MigolExecutionSession session) throws MigolExecutionException {
        while (session.getPP() > 0 && session.getPP() <= size()) {
            MigolStatement next = statements[session.getPP() - 1];
            if (next != null) {
                next.executeStatement(session);
            } else {
                throw new MigolExecutionException("Null statement found",session.getPP());
            }
        }
    }
    /**
     * Executes a single step of this program on the given session.
     * The step to be executed is determined by the value of the program
     * pointer of the {@link MigolExecutionSession} object.
     * @param session   The session which will be manipulated in this program
     * execution.
     * @throws se.migomipo.migol2.execute.MigolExecutionException
     */
    public void executeStep(MigolExecutionSession session) throws MigolExecutionException {
        int currentpp = session.getPP();
        if (currentpp <= 0 || currentpp > size()) {
            return;
        }
        MigolStatement next = statements[currentpp - 1];
        if (next != null) {
            next.executeStatement(session);
        } else {
            throw new MigolExecutionException("Null statement found",currentpp);
        }
    }

    /**
     * Creates a new MigolParsedProgram object containing the statements
     * in the given argument.
     * @param statements    The statements in the new program.
     * @see MigolStatement
     */
    public MigolParsedProgram(MigolStatement[] statements) {
        this.statements = statements;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MigolParsedProgram other = (MigolParsedProgram) obj;
        if (this.statements != other.statements && (this.statements == null || !Arrays.equals(this.statements, other.statements))) {
            return false;
        }
        return true;
    }

    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.statements != null ? this.statements.hashCode() : 0);
        return hash;
    }
}
