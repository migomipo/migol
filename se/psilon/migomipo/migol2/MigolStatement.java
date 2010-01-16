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
package se.psilon.migomipo.migol2;

import java.io.Serializable;
import se.psilon.migomipo.migol2.execute.MigolExecutionSession;
import se.psilon.migomipo.migol2.execute.MigolExecutionException;

/**
 * This interface represents an executable Migol statement.
 * @author John Eriksson
 */
public interface MigolStatement extends Serializable{
    /**
     * Executes the statement.
     * @param session   The session object to which the statement will be
     * performed.
     * @throws se.psilon.migomipo.migol2.execute.MigolExecutionException
     * if an error occurs during execution.
     */
    public void executeStatement(MigolExecutionSession session) throws MigolExecutionException;
    /**
     * Returns a Migol string representation of this statement.
     *
     * This method can be used to convert parsed statement back to a Migol text
     * program.
     * @return  A Migol syntax string of this statement.
     */
    public String toMigolSyntax();

    public MigolValue getTarget();
    public AssignmentOperation[] getOperations();
}
