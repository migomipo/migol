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
 * Represents the program pointer or a program pointer-deferred expression.
 *
 * Examples are # (refers to program pointer) and [#] (returns the current value
 * of the program pointer).
 *
 * Since # is not a valid immediate value, an exception is thrown when a
 * program tries to fetch the value from it. It can only be used to represent
 * the program pointer in an assignment statement. The reason for this is that
 * # is a pointer to the program counter, and has no integer value by itself.
 * @author John Eriksson
 */
public class ProgramPointerValue implements MigolValue {
    private static final long serialVersionUID = 3919553492099024992L;

    /**
     * The number of deferring steps.
     */
    private int defers;

    /**
     * Creates a new object, representing the program pointer or an immediate
     * value.
     * @param defer The number of dereferences.
     */
    public ProgramPointerValue(int defer) {
        this.defers = defer;
    }

    /**
     * Returns the number of deferring steps.
     * @return  The number of deferring steps as an integer.
     */
    public int getDefers() {
        return defers;
    }

    /**
     * Fetches the value of this expression in the session.
     *
     * Throws an {@link se.psilon.migomipo.migol2.execute.MigolExecutionException
     * MigolExecutionException} if it encounters a negative memory address
     * deferring or tries to use the program pointer as an immediate value.
     * @param session   The session object which will be manipulated.
     * @return  The resulting integer.
     * @throws se.psilon.migomipo.migol2.execute.MigolExecutionException   If the object represents #
     * @throws se.psilon.migomipo.migol2.execute.MigolExecutionException   If the deferring encounters a negative memory address
     */
    public int fetchValue(MigolExecutionSession session) throws MigolExecutionException {
        if (defers == 0) {
            throw new MigolExecutionException("Program pointer was incorrectly used as an immediate value at statement " + session.getPP(),session.getPP());
        }
        int[] memory = session.getMemory();
        int temp = session.getPP();
        for (int i = 1; i < defers; i++) {
            if (temp < 0) {
                throw new MigolExecutionException("Attempted to read a negative memory address at statement " + session.getPP(),session.getPP());
            }
            temp = memory[temp];

        }
        return temp;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toMigolSyntax() {

        StringBuffer buff = new StringBuffer();
        for(int i=0;i<defers;i++){
            buff.append('[');
        }
        buff.append('#');
        for(int i=0;i<defers;i++){
            buff.append(']');
        }
        return buff.toString();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProgramPointerValue other = (ProgramPointerValue) obj;
        if (this.defers != other.defers) {
            return false;
        }
        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + this.defers;
        return hash;
    }



}
