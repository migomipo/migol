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
package se.migomipo.migol;

import se.migomipo.migol.io.*;
import java.util.*;
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
    private int ret = -1;
    private int result = -1;
    private int handler = -1;
    private boolean isInterruptMode = false;
    private boolean debug = false;
    
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
               
    }

    public boolean getPPLocked() {
        return pplocked;
    }


    public void setPPLocked(boolean bool) {
        pplocked = bool;
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

    public boolean getInterruptMode() {
        return isInterruptMode;
    }

    public void setInterruptMode(boolean b) {
        isInterruptMode = b;
    }

    public int getReturn() {
        return ret;
    }

    public void setReturn(int i) {
        ret = i;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getHandler() {
        return handler;
    }

    public void setHandler(int handler) {
        this.handler = handler;
    }

    public boolean isWaitInterrupt() {
        return waitInterrupt;
    }

    public void setWaitInterrupt(boolean waitInterrupt) {
        this.waitInterrupt = waitInterrupt;
    }

    public boolean isDebugMode() {
        return debug;
    }

    public void setDebugMode(boolean debug) {
        this.debug = debug;
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
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new MigolExecutionException("Memory index out of bounds" ,ex, pp);
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

    public void addIOFunction(int i, MigolIOFunction func) {
        ioFunctions.put(i, func);
    }

    public MigolIOFunction getIOFunction(int i) {
        return ioFunctions.get(i);
    }
    
    public Map<Integer, MigolIOFunction> getIOFunctionMap(){
        return ioFunctions;
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
