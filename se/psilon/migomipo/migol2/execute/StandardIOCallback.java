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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;


/**
 * The standard I/O callback methods, used by the command line Migol program.
 * 
 * It reads data from {@code System.in} and prints to {@code System.out}
 *
 * @author John Eriksson
 */
public class StandardIOCallback implements MigolIOCallback {
    private Reader read;
    private PrintStream write;
    /**
     * Creates a new standard IO callback object.
     */
    public StandardIOCallback(){
        read = new InputStreamReader(System.in);
        write = System.out;        
    }
    /**
     * {@inheritDoc MigolIOCallback}
     */
    public int inputValue() throws IOException {
        return read.read();
    }
    /**
     * {@inheritDoc MigolIOCallback}
     */
    public void outputChar(int value) throws IOException {
        write.print((char) value);
    }
    /**
     * {@inheritDoc MigolIOCallback}
     */
    public void outputInt(int value) throws IOException {
        write.print(value);
    }

}
