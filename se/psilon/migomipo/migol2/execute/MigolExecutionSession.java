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

import java.util.*;
import se.psilon.migomipo.migol2.*;
import java.util.concurrent.LinkedBlockingQueue;

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
    private int swip = 0;
    private int ret = 0;
    private int mode = 0;
    private MigolSpecialRegister[] specialregisters;
    private boolean pplocked;
    private LinkedBlockingQueue<MigolInterrupt> interrupts = new LinkedBlockingQueue<MigolInterrupt>();
    private boolean waitInterrupt = false;


    public MigolExecutionSession() {
        this(1024 * 1024);
    }

    public MigolExecutionSession(int memsize) {        
        memory = new int[memsize];
        specialregisters = new MigolSpecialRegister[48];
        specialregisters[0] = new BranchSpecialRegister();
        specialregisters[1] = new SoftwareInterruptManagerAddressRegister();
        specialregisters[2] = new InterruptReturnAddressRegister();

        specialregisters[3] = new InterruptFlagRegister();
        specialregisters[4] = new ConsoleInputRegister();
        specialregisters[5] = new InterruptWaitRegister();
        TimerInterruptManager timer = new TimerInterruptManager();
        specialregisters[16] = timer.getTimerConfigRegister();
        specialregisters[17] = timer.getHandlerAddressRegister();

    }

    public boolean getPPLocked() {
        return pplocked;
    }

    public void setPPLocked(boolean bool) {
        pplocked = bool;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public LinkedBlockingQueue<MigolInterrupt> getInterruptQueue() {
        return interrupts;
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
        while (pp > 0 && pp <= program.size()) {         
            pplocked = false;
            MigolStatement next = program.getStatement(pp);
            next.executeStatement(this);           
            if (!pplocked) {
                pp++;
            }
            if (mode == 0) {
                MigolInterrupt interrupt;            
                if(waitInterrupt){
                    waitInterrupt = false;
                    try {
                        interrupt = interrupts.take();
                    } catch(InterruptedException ex){
                        throw new MigolExecutionException("Interrupt waiting interrupted by runtime at statement " + pp, ex, pp);
                    }
                } else {
                    interrupt = interrupts.poll();
                }
                if(interrupt != null){
                    interrupt.execute(this);
                }
            }
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
                throw new MigolExecutionException("Unmapped register " + position + " read at statement " + pp, pp);
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
                throw new MigolExecutionException("Unmapped register " + position + " written at statement " + pp, pp);
            }
        }
    }
    
    public void doInterrupt(int pos) {
        this.ret = pp;
        this.pp = pos;
        this.mode = 1;
    }
    
    public void returnInterrupt(){
        this.mode = 0;
        this.pp = ret;
        this.pplocked = true;
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

   
    private static class InterruptReturnAddressRegister implements MigolSpecialRegister {

        public InterruptReturnAddressRegister() {
        }

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return session.getRet();
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            session.setRet(val);
        }
    }

    private static class InterruptFlagRegister implements MigolSpecialRegister {
        private class SoftwareInterrupt implements MigolInterrupt {

            public void execute(MigolExecutionSession session) {
                session.doInterrupt(session.swip);
            }
            
        }
        private SoftwareInterrupt i;

        public InterruptFlagRegister() {
            this.i = new SoftwareInterrupt();
        }

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return session.getMode();
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            int oldmode = session.getMode();
            int newmode = val;
            if (oldmode == 0 && newmode == 1) {
                session.getInterruptQueue().add(i);
            } else if (oldmode == 1 && newmode == 0) {
                session.returnInterrupt();              
            } else if (newmode != 0 && newmode != 1) {
                throw new MigolExecutionException(session.getPP());
            }

        }
    }

    
    
    private static class SoftwareInterruptManagerAddressRegister implements MigolSpecialRegister {

        public SoftwareInterruptManagerAddressRegister() {
        }

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return session.swip;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            session.swip = val;
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
    
    
}
