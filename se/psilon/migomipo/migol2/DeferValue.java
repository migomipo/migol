package se.psilon.migomipo.migol2;

public class DeferValue implements ReadValue {


    private WriteValue value;
    private int defers;

    public DeferValue(WriteValue value, int defers) {

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



}
