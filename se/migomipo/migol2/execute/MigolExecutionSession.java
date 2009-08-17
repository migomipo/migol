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
package se.migomipo.migol2.execute;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Represents a unique execution session.
 *
 * Session objects contain the current state of a running program, which is
 * the memory and the program counter. It also specifies the input and output
 * streams which will be used in the program.
 * @see se.migomipo.migol2.MigolParsedProgram
 * @author John Eriksson
 */
public class MigolExecutionSession {
    /**
     * Contains the data memory.
     */
    private int[] memory;
    /**
     * The program pointer.
     */
    private int pp;
    private MigolIOCallback iocallback;

    public MigolExecutionSession() {
        this(new StandardIOCallback());
    }

    public MigolExecutionSession(int memsize) {
        this(memsize, new StandardIOCallback());
    }
    
    public MigolExecutionSession(MigolIOCallback iocallback) {
        this(1024 * 1024, iocallback);
    }
    
    public MigolExecutionSession(int memsize, MigolIOCallback iocallback){
        pp = 1;
        memory = new int[memsize];
        this.iocallback = iocallback;
    }

    
    /**
     * Resets the data memory and the program counter.
     * @return  This session object.
     */
    public MigolExecutionSession reset() {
        pp = 1;
        memory = new int[memory.length];
        return this;
    }

    /**
     * Returns the data memory array for the session as an array of 32-bit
     * signed integers.
     * @return  The data memory for this session.
     */
    public int[] getMemory() {
        return memory;
    }
    /**
     * Returns the current value of the program pointer.
     * @return  The current value of the program pointer.
     */
    public int getPP() {
        return pp;
    }
    /**
     * Sets the value of the program pointer.
     * @param val   The new value.
     */
    public void setPP(int val) {
        pp = val;
    }
    /**
     * Moves the program pointer one statement forward.
     */
    public void progressPP() {
        pp++;
    }
    /**
     * Returns the {@link se.migomipo.migol2.execute.MigolIOCallback} object for
     * this session.
     * @return  The {@link se.migomipo.migol2.execute.MigolIOCallback} object.
     */
    public MigolIOCallback getIOCallback() {
        return iocallback;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MigolExecutionSession other = (MigolExecutionSession) obj;
        if (this.memory != other.memory && (this.memory == null || !Arrays.equals(this.memory,other.memory))) {
            return false;
        }
        if (this.pp != other.pp) {
            return false;
        }
        if (this.iocallback != other.iocallback && (this.iocallback == null || !this.iocallback.equals(other.iocallback))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.memory != null ? this.memory.hashCode() : 0);
        hash = 89 * hash + this.pp;
        hash = 89 * hash + (this.iocallback != null ? this.iocallback.hashCode() : 0);
        return hash;
    }

    /**
     * Writes the current state of this session to a {@code OutputStream}.
     * @param out   The output stream which the state will be written to.
     * @throws java.io.IOException
     */
    public void saveState(OutputStream out) throws IOException {
        DataOutputStream dout = new DataOutputStream(out);
        dout.writeInt(pp);
        dout.writeInt(memory.length);
        for(int i : memory){
            dout.writeInt(i);
        }

    }

    /**
     * Loads state from a {@code InputStream} to this session object.
     *
     * It is suitable to load data which have been saved with the
     * {@link MigolExecutionSession#saveState(java.io.OutputStream) } method.
     * @param in
     * @throws java.io.IOException
     */
    public void loadState(InputStream in) throws IOException {
        DataInputStream din = new DataInputStream(in);
        pp = din.readInt();
        memory = new int[din.readInt()];
        for(int i = 0;i<memory.length;i++){
            memory[i] = din.readInt();
        }
    }

}
