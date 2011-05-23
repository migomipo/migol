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
    private int swip = 0;
    private int ret = 0;
    private MigolInterrupt curInterrupt = null;
    private MigolSpecialRegister[] specialregisters;
    private boolean pplocked;
    private LinkedBlockingQueue<MigolInterrupt> interrupts = new LinkedBlockingQueue<MigolInterrupt>();
    private boolean waitInterrupt = false;
    private IOManager io;

    public MigolExecutionSession() {
        this(1024 * 1024);
    }

    public MigolExecutionSession(int memsize) {
        memory = new int[memsize];
        specialregisters = new MigolSpecialRegister[64];
        specialregisters[0] = new BranchSpecialRegister();
        specialregisters[1] = new SoftwareInterruptManagerAddressRegister();
        specialregisters[2] = new InterruptReturnAddressRegister();

        specialregisters[3] = new InterruptFlagRegister();
        specialregisters[4] = new ConsoleInputRegister();
        specialregisters[5] = new InterruptWaitRegister();
        TimerInterruptManager timer = new TimerInterruptManager();
        specialregisters[9] = timer.new MilliClockRegister();
        specialregisters[10] = timer.new TimerInterruptConfigRegister();
        specialregisters[11] = timer.new TimerInterruptHandlerRegister();
        specialregisters[12] = timer.new TimerInterruptTimeRegister();

    }

    public boolean getPPLocked() {
        return pplocked;
    }

    private void setupIO() {
        io = new IOManager();
        FileOperationManager file = new FileOperationManager(io);
        SocketManager soc = new SocketManager(io);
        specialregisters[20] = io.getHandlerAddressRegister();
        specialregisters[21] = io.getBufferAddressRegister();
        specialregisters[22] = io.getBufferLengthRegister();
        specialregisters[23] = io.getIoHandleRegister();
        specialregisters[24] = io.getReadRequestRegister();
        specialregisters[25] = io.getWriteRequestRegister();
        specialregisters[26] = io.getCloseHandleRegister();
        specialregisters[27] = io.getInterruptHandleRegister();
        specialregisters[28] = io.getInterruptBufferAddressRegister();
        specialregisters[29] = io.getInterruptBufferLengthRegister();
        specialregisters[30] = io.getInterruptTypeRegister();
        specialregisters[31] = io.getIOErrnoRegister();

        specialregisters[40] = file.getFileNameAddressRegister();
        specialregisters[41] = file.getFileNameLengthRegister();
        specialregisters[42] = file.getFileOpenRegister();
        specialregisters[43] = file.getFileModeRegister();
        specialregisters[44] = file.getFileOpenInterruptNameAddressRegister();
        specialregisters[45] = file.getFileOpenInterruptNameLengthRegister();
        specialregisters[46] = file.getFileOpenInterruptHandleRegister();
        specialregisters[47] = file.getFileOpenInterruptErrorRegister();
        
        specialregisters[48] = file.getFileSeekRegister();
        specialregisters[49] = file.getFileSizeRegister();
        

        specialregisters[50] = soc.getOpenSocketInterruptHandlerRegister();
        specialregisters[51] = soc.getIpAddressAddressRegister();
        specialregisters[52] = soc.getPortRegister();
        specialregisters[53] = soc.getIpAddressModeRegister();
        specialregisters[54] = soc.getOpenSocketRegister();
        specialregisters[55] = soc.getIntHandleRegister();
        specialregisters[56] = soc.getIntIpAddrAddressRegister();
        specialregisters[57] = soc.getIntIpPortRegister();
        specialregisters[58] = soc.getIntErrnoRegister();
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

    public MigolInterrupt getCurInterrupt() {
        return curInterrupt;
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
        try {
            setupIO();
            while (pp > 0 && pp <= program.size()) {
                pplocked = false;
                MigolStatement next = program.getStatement(pp);
                next.executeStatement(this);
                if (!pplocked) {
                    pp++;
                }
                if (curInterrupt == null) {
                    MigolInterrupt i;
                    if (waitInterrupt) {
                        waitInterrupt = false;
                        try {
                            i = interrupts.take();
                        } catch (InterruptedException ex) {
                            throw new MigolExecutionException(
                                    "Interrupt waiting interrupted by runtime", ex, pp);
                        }
                    } else {
                        i = interrupts.poll();
                    }
                    if (i != null) {
                        doInterrupt(i);
                    }
                }
            }
        } finally {
            io.close();
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

    private void doInterrupt(MigolInterrupt i) {
        this.ret = pp;
        this.pp = i.getHandlerAddress(this);
        this.curInterrupt = i;
    }

    private void returnInterrupt() {
        this.curInterrupt = null;
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

    private static class BranchSpecialRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return session.getPP();
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            session.setPP(val);
            session.setPPLocked(true);
        }
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

            public int getHandlerAddress(MigolExecutionSession session) {
                return session.swip;
            }

        }
        private SoftwareInterrupt i;

        public InterruptFlagRegister() {
            this.i = new SoftwareInterrupt();
        }

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return (session.curInterrupt == null) ? 0 : 1;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            int oldmode = (session.curInterrupt == null) ? 0 : 1;
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
