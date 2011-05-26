
package se.psilon.migomipo.migol2;

public class InterruptWaitValue implements WriteValue {

    private static final InterruptWaitValue instance = new InterruptWaitValue();

    public static InterruptWaitValue getInstance() {
        return instance;
    }

    public int defer(MigolExecutionSession session) throws MigolExecutionException {
        return 0;
    }

    public void set(MigolExecutionSession session, int val) throws MigolExecutionException {
        session.setWaitInterrupt(true);

    }

}
