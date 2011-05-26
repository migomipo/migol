

package se.psilon.migomipo.migol2;

public class BranchValue implements WriteValue {

    private static final BranchValue instance = new BranchValue();

    public static BranchValue getInstance() {
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
