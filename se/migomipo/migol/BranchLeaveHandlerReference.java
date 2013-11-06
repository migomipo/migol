package se.migomipo.migol;

public class BranchLeaveHandlerReference implements MigolReference {

    private static final BranchLeaveHandlerReference instance = new BranchLeaveHandlerReference();

    private BranchLeaveHandlerReference(){}

    public static BranchLeaveHandlerReference getInstance() {
        return instance;
    }

    public int defer(MigolExecutionSession session) throws MigolExecutionException {
        return session.getPP();
    }

    public void set(MigolExecutionSession session, int val) throws MigolExecutionException {
        session.setPP(val);
        session.setPPLocked(true);
        session.setInterruptMode(false);
        session.setReturn(-1);
        session.setResult(-1);
    }





}
