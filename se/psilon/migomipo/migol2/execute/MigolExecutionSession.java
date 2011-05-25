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

import se.psilon.migomipo.migol2.io.*;
import java.util.*;
import se.psilon.migomipo.migol2.*;
import java.util.concurrent.LinkedBlockingQueue;
import se.psilon.migomipo.migol2.io.IOManager;

/**
 * Represents a unique execution session.
 *
 * Session objects contain the current state of a running program, which is
 * the memory and the program counter. It also specifies the input and output
 * streams which will be used in the program.
 * @see se.psilon.migomipo.migol2.MigolParsedProgram
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
    private int pp = 1;
    private int ret = -1;
    private int result = -1;
    private int handler = -1;
    private boolean isInterruptMode = false;
    private MigolSpecialRegister[] specialregisters;
    private boolean pplocked = false;
    private LinkedBlockingQueue<Integer> results = new LinkedBlockingQueue<Integer>();
    private Map<Integer, MigolIOFunction> ioFunctions = new HashMap<Integer, MigolIOFunction>();
    private boolean waitInterrupt = false;
    private boolean hasRun = false;


    public MigolExecutionSession() {
        this(1024 * 1024);
    }

    public MigolExecutionSession(int memsize) {
        memory = new int[memsize];
        specialregisters = new MigolSpecialRegister[8];
        specialregisters[0] = new BranchRegister();
        specialregisters[1] = new BranchLeaveHandlerRegister();
        specialregisters[2] = new ExecRegister();
        specialregisters[3] = new InterruptHandlerRegister();
        specialregisters[4] = new InterruptResultRegister();
        specialregisters[5] = new InterruptReturnAddressRegister();
        specialregisters[6] = new InterruptWaitRegister();
        specialregisters[7] = new ConsoleInputRegister();
        

    }

    public boolean getPPLocked() {
        return pplocked;
    }


    public void setPPLocked(boolean bool) {
        pplocked = bool;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public LinkedBlockingQueue<Integer> getResultQueue() {
        return results;
    }
  

    /**
     * Returns the data memory array for the session as an array of 32-bit
     * signed integers.
     *
     * The array is returned by reference.
     * All changes to the array will affect this session.
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
     * Executes the program with the given session.
     *
     * The session will not be reset. If an old session object is used, the old
     * memory content will still be preserved when execution starts.
     * @param session   The session which will be manipulated in this program
     * execution.
     * @throws se.psilon.migomipo.migol2.execute.MigolExecutionException
     * If an error occurs during execution.
     */
    public void executeProgram(MigolParsedProgram program) throws MigolExecutionException {
        if(hasRun){
            throw new MigolExecutionException("Session object is in a" +
                    " finished state");
        }

        try {
            while (pp > 0 && pp <= program.size()) {
                pplocked = false;
                MigolStatement next = program.getStatement(pp);
                next.executeStatement(this);
                if (!pplocked) {
                    pp++;
                }
                if (!isInterruptMode) {
                    Integer _result;
                    if (waitInterrupt) {
                        waitInterrupt = false;
                        try {
                            _result = results.take();
                        } catch (InterruptedException ex) {
                            throw new MigolExecutionException(
                                    "Interrupt waiting interrupted by runtime", ex, pp);
                        }
                    } else {
                        _result = results.poll();
                    }
                    if (_result != null) {
                        this.ret = pp;
                        this.pp = handler;
                        this.result = _result.intValue();
                        this.isInterruptMode = true;
                    }
                }
            }
        } finally {
        }
    }

    /**
     * Performs a memory deferring operation. If the memory address is negative,
     * a special register operation will be performed.
     *
     * @param position  The memory register to be deferred
     * @return          The resulting value
     */
    public int registerGet(int position) throws MigolExecutionException {
        if (position >= 0) {
            return memory[position];
        } else {
            try {
                int p = (-1) - position;
                return specialregisters[p].read(this);
            } catch (NullPointerException ex) {
                throw new MigolExecutionException("Unmapped register " + position + " read from", pp);
            }
        }
    }

    /**
     * Writes a value to memory. If the memory address is negative,
     * a special register operation will be performed.
     *
     * @param position  The memory register to be written to
     * @param value     The value to be written to memory
     */
    public void registerPut(int position, int value) throws MigolExecutionException {
        if (position >= 0) {
            memory[position] = value;
        } else {
            try {
                int p = (-1) - position;
                specialregisters[p].write(this, value);
            } catch (NullPointerException ex) {
                throw new MigolExecutionException("Unmapped register " + position + " written to", pp);
            }
        }
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
        if (!Arrays.equals(this.memory, other.memory)) {
            return false;
        }
        if (this.pp != other.pp) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Arrays.hashCode(this.memory);
        hash = 29 * hash + this.pp;
        return hash;
    }

    private static class BranchRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return session.pp;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            session.pp = val;
            session.pplocked = true;
        }
    }

    private static class BranchLeaveHandlerRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return session.pp;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            session.pp = val;
            session.pplocked = true;
            session.isInterruptMode = false;
            session.ret = -1;
            session.result = -1;

        }

    }

    private static class InterruptReturnAddressRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return session.ret;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }

    }

    private static class InterruptResultRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return session.result;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    private static class InterruptWaitRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            session.waitInterrupt = true;
            return 0;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    private static class ExecRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return 0;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            MigolIOFunction func;
            try {
                func = session.ioFunctions.get(session.memory[val]);
                func.executeIO(session, val);
            } catch(NullPointerException ex){
                throw new MigolExecutionException(session.pp);
            }
            
        }
    }

    private static class InterruptHandlerRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return session.handler;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            session.handler = val;
        }

    }

    /*
     BRANCH    # 
     RETURN    *# 
     RESULT    *! 
     BREINT    #! 
     STDIN     @  
     WAIT      \  
     EXEC      !  
     HANDLER   !#
     *
    */
}
