
package se.migomipo.migol;

public class InterruptWaitReference implements MigolReference {

    private static final InterruptWaitReference instance = new InterruptWaitReference();

    private InterruptWaitReference(){}

    public static InterruptWaitReference getInstance() {
        return instance;
    }

    public int defer(MigolExecutionSession session) throws MigolExecutionException {
        return 0;
    }

    public void set(MigolExecutionSession session, int val) throws MigolExecutionException {
        session.setWaitInterrupt(true);

    }

}
