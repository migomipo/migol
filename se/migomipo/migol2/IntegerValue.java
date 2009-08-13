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
 * Represents an integer-addressed or immediate value expression.
 *
 * It is an immediate value if it has no deferring (square brackets), otherwise
 * it is a dereferring expression.
 * Examples of these are 4 (immediate value) and [[6]]
 * (double-deferred value). Values can be any signed 32-bit integer, but
 * reading negative memory addresses is not permitted.
 *
 * The expression "5" will simply return a 32-bit signed integer with the value
 * 5. [5] reads the value in memory address 5 and returns it. [[5]] uses two
 * steps of deferring and returns the value in the address in address 5.
 * Integers can be used interchangeably as numbers and pointers.
 *
 *
 * @author John Eriksson
 */
public class IntegerValue implements MigolValue {
    private static final long serialVersionUID = 2817122896753151636L;
    /**
     * The number of deferring steps.
     */
    private final int defers;

    /*
     * The integer value.
     */
    private final int value;
    /*
     * Returns the number of dereferences.
     */
    public int getDefers() {
        return defers;
    }
    /**
     * Returns the internal integer value.
     * @return  The integer.
     */
    public int getValue() {
        return value;
    }




    /**
     * Creates a new object, representing an integer-addressed or immediate
     * value.
     * @param defer The number of dereferences.
     * @param value The integer value.
     */
    public IntegerValue(int value,int defer){
        this.defers = defer;
        this.value = value;

    }
    /**
     * Fetches the current value of this expression from the
     * {@link MigolExecutionSession session} object.
     *
     * It will defer the value as many times as specified in the current object.
     * If it is 0, it will just return the integer value.
     * @param session   The session that the value is fetched from.
     * @return  The resulting integer.
     * @throws se.migomipo.migol2.execute.MigolExecutionException   If the
     * deferring encounters a negative memory address
     */
    public int fetchValue(MigolExecutionSession session) throws MigolExecutionException {
        try {
        int temp = value;
        int[] mem = session.getMemory();
        for(int i=0;i<defers;i++){
            if(temp < 0) throw new MigolExecutionException("Attempted to read a negative memory address at statement " + session.getPP());
            temp = mem[temp];
            // No recursion! A huge improvement over the older, more crappy
            // interpreter.
        }
        return temp;
        } catch(ArrayIndexOutOfBoundsException ex){
            throw new MigolExecutionException("Memory access exceeded available memory at statement " + session.getPP());
        }
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
        final IntegerValue other = (IntegerValue) obj;
        if (this.defers != other.defers) {
            return false;
        }
        if (this.value != other.value) {
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
        hash = 59 * hash + this.defers;
        hash = 59 * hash + this.value;
        return hash;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toMigolSyntax() {
        String number = Integer.toString(value);
        StringBuffer buff = new StringBuffer();
        for(int i=0;i<defers;i++){
            buff.append('[');
        }
        buff.append(number);
        for(int i=0;i<defers;i++){
            buff.append(']');
        }
        return buff.toString();
    }



}
