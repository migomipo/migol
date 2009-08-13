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
import se.migomipo.migol2.execute.MigolIOExecutionException;

/**
 * Represents the input buffer.
 *
 * It is mostly used as [&#64;], when reading a value from input. It can also
 * theoretically be used with several defering steps, such as [[&#64;]], which
 * returns the value as the address returned by input.
 *
 * It can NOT be used as an immediate value. If the program tries to read from
 * or assign to @, an exception will be thrown.
 *

 * @author John Eriksson
 */
public class InputBufferValue implements MigolValue {
    private static final long serialVersionUID = -5534385060781209289L;
    /**
     * The number of deferring steps.
     */
    private final int defers;
    /**
     * Creates a new object, representing an input buffer-deferring
     * value expression.
     * @param defer The number of dereferences.
     */
    public InputBufferValue(int defer){
        this.defers = defer;
    }
    /**
     * Returns the number of deferring steps.
     * @return  Tthe number of deferring steps as an integer.
     */
    public int getDefers() {
        return defers;
    }
    /**
     * Fetches the value of this expression in the session.
     *
     * Throws an {@link se.migomipo.migol2.execute.MigolExecutionException
     * MigolExecutionException} if it encounters a negative memory address while
     * deferring or tries to use the input buffer pointer as an immediate value.
     * @param session
     * @return  The resulting integer.
     * @throws se.migomipo.migol2.execute.MigolExecutionException   If the object represents @ and is used as an immediate value.
     * @throws se.migomipo.migol2.execute.MigolExecutionException   If the deferring encounters a negative memory address
     */
    public int fetchValue(MigolExecutionSession session) throws MigolExecutionException {

        if(defers == 0) throw new MigolExecutionException("Invalid input value usage at statement " + session.getPP());
        int temp = 0;
        int[] memory = session.getMemory();
        try {
            temp = session.getIOCallback().inputValue();
        } catch(Exception e){
             throw new MigolIOExecutionException("I/O operation failed at statement " + session.getPP(),e);
        }
        for(int i=1;i<defers;i++){
            if(temp < 0) throw new MigolExecutionException("Attempted to read a negative memory address at statement " + session.getPP());
            temp = memory[temp];

        }
        return temp;
    }

    

    public String toMigolSyntax() {

        StringBuffer buff = new StringBuffer();
        for(int i=0;i<defers;i++){
            buff.append('[');
        }
        buff.append('@');
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
        final InputBufferValue other = (InputBufferValue) obj;
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
        int hash = 7;
        hash = 79 * hash + this.defers;
        return hash;
    }



}
