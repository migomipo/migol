package se.psilon.migomipo.migol2.execute;

/**
 *
 * @author John Eriksson
 */
public class BranchSpecialRegister implements MigolSpecialRegister {
    private MigolExecutionSession session;

    public BranchSpecialRegister(MigolExecutionSession session) {
        this.session = session;
    }

    public int read(MigolExecutionSession session) throws MigolExecutionException {
        return session.getPP();
    }

    public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        session.setPP(val);
        session.setPPLocked(true);
    }

}
