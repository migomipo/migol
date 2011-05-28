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


import java.util.HashMap;
import java.util.Map;
import java.lang.ref.*;


/**
 * Represents an accessible value in the Migol environment.
 *
 * The memory in Migol is an array of 32-bit signed integers. Integers can be
 * use to address memory.
 * @author John Eriksson
 */
public class IntegerValue implements java.io.Serializable, MigolValue {

    private static final Map<Integer, WeakReference<IntegerValue>> instances =
            new HashMap<Integer, WeakReference<IntegerValue>>();

    public static IntegerValue getInstance(int i){
        IntegerValue val = null;
        WeakReference<IntegerValue> ref = instances.get(i);
        if(ref != null){
            val = ref.get();
        }
        if(val == null){
            val = new IntegerValue(i);
            instances.put(i, new WeakReference<IntegerValue>(val));
        }
        return val;


    }

    private final int value;

    private IntegerValue(int value) {
        
        this.value = value;
    }

    public int getInternalValue() {
        return value;
    }

    public int get(MigolExecutionSession session) {
        return value;
    }

    public int defer(MigolExecutionSession session) {
        return session.getMemory()[value];
    }

    public void set(MigolExecutionSession session, int val) {
        session.getMemory()[value] = val;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IntegerValue other = (IntegerValue) obj;
        if (this.value != other.value) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return this.value;
    }



}
