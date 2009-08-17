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
import se.migomipo.migol2.execute.MigolExecutionSession;
import se.migomipo.migol2.execute.MigolExecutionException;

/**
 * Defines a conditional operation.
 *
 * Each conditional operation can be evaluated with a
 * {@link se.migomipo.migol2.execute.MigolExecutionSession}, and return true or
 * false, likely using the current session data for the evaluation.
 *
 *
 *
 * @author John Eriksson
 */
public interface ConditionalOperation extends Serializable {

    /**
     * Evaluates the expression on the session given as an argument, and returns
     * the result.
     * @param session   The session where the expression is evaluated.
     * @return  The result of the expression, as a boolean.
     * @throws se.migomipo.migol2.execute.MigolExecutionException
     * If an error occurs during the operation.
     */
    public boolean evaluate(MigolExecutionSession session) throws MigolExecutionException;
    /**
     * Returns a Migol syntax string representation of this conditional operation
     * as a conditional suffix.
     * @return  The conditional operation as a Migol syntactic string.
     */
    public String toMigolSyntax();
}
