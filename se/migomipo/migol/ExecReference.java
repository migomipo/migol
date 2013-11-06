package se.migomipo.migol;

import se.migomipo.migol.io.MigolIOFunction;

public class ExecReference implements MigolReference {

    private static final ExecReference instance = new ExecReference();

    private ExecReference(){};

    public static ExecReference getInstance() {
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
