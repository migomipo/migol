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

import se.psilon.migomipo.migol2.execute.MigolExecutionSession;
import se.psilon.migomipo.migol2.execute.MigolExecutionException;

/**
 * Represents an accessible value in the Migol environment.
 *
 * The memory in Migol is an array of 32-bit signed integers. Integers can be
 * use to address memory.
 * @author John Eriksson
 */
public class MigolValue implements java.io.Serializable {

    /**
     * The number of deferring steps.
     */
    private final int defers;

    /*
     * The integer value.
     */
    private final int value;

    public MigolValue(int value, int defers) {
        this.defers = defers;
        this.value = value;
    }

    /**
     * Returns the resulting memory value.
     * @param session     The session used for fetching the value.
     * @return  The resulting value as an 32-bit signed integer (as Java defines
     * the int data type).
     * @throws se.psilon.migomipo.migol2.execute.MigolExecutionException  If an error
     * occurs while fetching the value.
     *
     */
    public int fetchValue(MigolExecutionSession session) throws MigolExecutionException {
        int temp = value;
        for (int i = 0; i < defers; i++) {
            temp = session.registerGet(temp);
            // No recursion! A huge improvement over the older, more crappy
            // interpreter.
        }
        return temp;
    }

    /**
     * Returns the number of deferring steps for the value.
     *
     * @return  The number of deferring steps.
     */
    public int getDefers() {
        return defers;
    }

    public int getInternalValue() {
        return value;
    }

    /**
     * Returns a Migol syntax string representation of this value,.
     * @return  A Migol syntax string representation of this value.
     */
    public String toMigolSyntax() {
        String number = Integer.toString(value);
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < defers; i++) {
            buff.append('[');
        }
        buff.append(number);
        for (int i = 0; i < defers; i++) {
            buff.append(']');
        }
        return buff.toString();
    }
}
