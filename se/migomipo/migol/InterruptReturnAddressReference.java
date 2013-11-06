
package se.migomipo.migol;

public class InterruptReturnAddressReference implements MigolReference {

    private static final InterruptReturnAddressReference instance = new InterruptReturnAddressReference();

    private InterruptReturnAddressReference(){}

    public static InterruptReturnAddressReference getInstance() {
        return instance;
    }

    public int defer(MigolExecutionSession session) throws MigolExecutionException {
        return session.getReturn();
    }

    public void set(MigolExecutionSession session, int val) throws MigolExecutionException {

    }

}
