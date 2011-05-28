package se.psilon.migomipo.migol2;

public class BranchLeaveHandlerReference implements MigolReference {

    private static final BranchLeaveHandlerReference instance = new BranchLeaveHandlerReference();

    private BranchLeaveHandlerReference(){}

    public static BranchLeaveHandlerReference getInstance() {
        return instance;
    }

    public int defer(MigolExecutionSession session) throws MigolExecutionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void set(MigolExecutionSession session, int val) throws MigolExecutionException {
        session.setPP(val);
        session.setPPLocked(true);
        session.setInterruptMode(false);
        session.setReturn(-1);
        session.setResult(-1);
    }





}
