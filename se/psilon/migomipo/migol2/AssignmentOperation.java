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
 * Represents a single assignment operation.
 *
 * These are executed in a sequence in a {@link AssignmentStatement}.
 *
 * @author John Eriksson
 * @see AssignmentStatement
 */
public interface AssignmentOperation extends Serializable {

    /**
     * Performs this operation.
     * @param session   The session object to which the statement will be performed.
     * @param currentvalue  The current value of the object to be modified.
     * @return  The result of this operation.
     * @throws se.psilon.migomipo.migol2.execute.MigolExecutionException
     * If an error occurs during the operation.
     */
    public int operation(MigolExecutionSession session, int currentvalue) throws MigolExecutionException;

    /**
     * Returns a Migol syntax string representation of this assignment
     * operation.
     * @return  The conditional operation as a Migol syntactic string.
     */
    public String toMigolSyntax();
}
