
package se.psilon.migomipo.migol2;

public class InterruptHandlerValue implements MigolReference {

    private static final InterruptHandlerValue instance = new InterruptHandlerValue();

    private InterruptHandlerValue(){}

    public static InterruptHandlerValue getInstance() {
        return instance;
    }

    public int defer(MigolExecutionSession session) throws MigolExecutionException {
        return session.getHandler();
    }

    public void set(MigolExecutionSession session, int val) throws MigolExecutionException {
        session.setHandler(val);
    }

}
