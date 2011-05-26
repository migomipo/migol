package se.psilon.migomipo.migol2;

import se.psilon.migomipo.migol2.io.MigolIOFunction;

public class ExecValue implements WriteValue {

    private static final ExecValue instance = new ExecValue();

    public static ExecValue getInstance() {
        return instance;
    }

    public int defer(MigolExecutionSession session) throws MigolExecutionException {
        return 0;
    }

    public void set(MigolExecutionSession session, int val) throws MigolExecutionException {
        MigolIOFunction func;
        try {
            func = session.getIOFunction(session.getMemory()[val]);
            func.executeIO(session, val);
        } catch (NullPointerException ex) {
            throw new MigolExecutionException("Undefined I/O function used", session.getPP());
        }
    }
}
