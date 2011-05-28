

package se.psilon.migomipo.migol2;

public class BranchReference implements MigolReference {

    private static final BranchReference instance = new BranchReference();

    private BranchReference(){}

    public static BranchReference getInstance() {
        return instance;
    }

    public int defer(MigolExecutionSession session) {
        return session.getPP();
    }

    public void set(MigolExecutionSession session, int val) {
        session.setPP(val);
        session.setPPLocked(true);
    }

}
