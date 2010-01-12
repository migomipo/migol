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

package se.psilon.migomipo.migol2.execute;

import se.psilon.migomipo.migol2.InputBufferValue;
import se.psilon.migomipo.migol2.OutputStatement;

/**
 * An interface representing the available I/O functions in Migol.
 * 
 * When an {@link OutputStatement} is executed, or an {@link InputBufferValue} 
 * is evaluated, the corresponding I/O callback methods are called. 
 *
 *
 * @author John Eriksson
 */
public interface MigolIOCallback {

    /**
     * Reads an integer and returns it.
     *
     * This method is called when a {@link InputBufferValue} ({@code [&#64;]})
     * is evaluated.
     * @return  The read value, as a 32-bit signed integer.
     * @throws java.io.IOException
     */
    public int inputValue() throws java.io.IOException;
    /**
     * Prints an integer as a ASCII character.
     *
     * This method is called when a {@link OutputStatement} in character printing
     * mode ({@code &gt;}) is called.
     * @param value     The character to be printed.
     * @throws java.io.IOException
     */
    public void outputChar(int value) throws java.io.IOException;
    /**
     * Prints an integer as a series of digits.
     *
     * This method is called when a {@link OutputStatement} in number printing
     * mode ({@code &gt;-}) is called.
     * @param value     The character to be printed.
     * @throws java.io.IOException
     */
    public void outputInt(int value) throws java.io.IOException;
}
