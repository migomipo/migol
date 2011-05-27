
package se.psilon.migomipo.migol2;

public class InterruptReturnAddressValue implements WriteValue {

    private static final InterruptReturnAddressValue instance = new InterruptReturnAddressValue();

    private InterruptReturnAddressValue(){}

    public static InterruptReturnAddressValue getInstance() {
        return instance;
    }

    public int defer(MigolExecutionSession session) throws MigolExecutionException {
        return session.getReturn();
    }

    public void set(MigolExecutionSession session, int val) throws MigolExecutionException {

    }

}
