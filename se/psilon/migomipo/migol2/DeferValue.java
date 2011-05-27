package se.psilon.migomipo.migol2;

import java.lang.ref.WeakReference;

import java.util.Map;
import java.util.WeakHashMap;

public class DeferValue implements ReadValue {

    private static final Map<DeferValue, WeakReference<DeferValue>> values =
            new WeakHashMap<DeferValue, WeakReference<DeferValue>>();

    public static DeferValue getInstance(WriteValue value, int defers){
        DeferValue v = new DeferValue(value, defers);
        WeakReference<DeferValue> v2 = values.get(v);
        DeferValue v3 = null;
        if(v2 != null){
            v3 = v2.get();
        }
        if(v3 == null){
            v3 = v;
            values.put(v, new WeakReference<DeferValue>(v));
        }

        return v3;
    }

    private WriteValue value;
    private int defers;

    private DeferValue(WriteValue value, int defers) {

        if(defers < 1){
            throw new IllegalArgumentException();
        }
        this.value = value;
        this.defers = defers;
    }


    public int get(MigolExecutionSession session) throws MigolExecutionException {
        int[] mem = session.getMemory();
        int address = value.defer(session);
        for(int i=1;i<defers;i++){
            address = mem[address];
        }
        return address;
    }

    public int defer(MigolExecutionSession session) throws MigolExecutionException {
        int[] mem = session.getMemory();
        return mem[get(session)];
    }

    public void set(MigolExecutionSession session, int val) throws MigolExecutionException {
        int[] mem = session.getMemory();
        mem[get(session)] = val;

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DeferValue other = (DeferValue) obj;
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        if (this.defers != other.defers) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 97 * hash + this.defers;
        return hash;
    }

}
