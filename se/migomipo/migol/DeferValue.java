package se.migomipo.migol;

import java.lang.ref.WeakReference;

import java.util.Map;
import java.util.WeakHashMap;

public class DeferValue implements MigolValue {

    private static final FlyweightStore<DeferValue> instances = new FlyweightStore<DeferValue>();

    public static DeferValue getInstance(MigolReference value, int defers){
        return instances.get(new DeferValue(value, defers));
    }

    private MigolReference ref;
    private int defers;

    private DeferValue(MigolReference value, int defers) {

        if(defers < 1){
            throw new IllegalArgumentException();
        }
        this.ref = value;
        this.defers = defers;
    }


    public int get(MigolExecutionSession session) throws MigolExecutionException {
        int[] mem = session.getMemory();
        int address = ref.defer(session);
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

    public int getDefers() {
        return defers;
    }

    public MigolReference getReference() {
        return ref;
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
        if (this.ref != other.ref && (this.ref == null || !this.ref.equals(other.ref))) {
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
        hash = 97 * hash + (this.ref != null ? this.ref.hashCode() : 0);
        hash = 97 * hash + this.defers;
        return hash;
    }

}
