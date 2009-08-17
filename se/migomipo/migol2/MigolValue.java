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

import se.migomipo.migol2.execute.MigolExecutionSession;
import se.migomipo.migol2.execute.MigolExecutionException;

/**
 * Represents an accessible value in the Migol environment.
 *
 * The memory in Migol is an array of 32-bit signed integers. There are
 * also some special registers, such as # (program pointer) and
 * &#64; (input buffer).
 * @author John Eriksson
 */
public interface MigolValue extends java.io.Serializable {
    /**
     * Returns the resulting memory value.
     * @param session     The session used for fetching the value.
     * @return  The resulting value as an 32-bit signed integer (as Java defines
     * the int data type).
     * @throws se.migomipo.migol2.execute.MigolExecutionException  If an error
     * occurs while fetching the value.
     *
     */
    public int fetchValue(MigolExecutionSession session) throws MigolExecutionException;
    
    /**
     * Returns the number of deferring steps for the value.
     *
     * @return  The number of deferring steps.
     */
    public int getDefers();
    /**
     * Returns a Migol syntax string representation of this value,.
     * @return  A Migol syntax string representation of this value.
     */
    public String toMigolSyntax();

}
