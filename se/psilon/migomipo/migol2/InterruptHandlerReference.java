
package se.psilon.migomipo.migol2;

public class InterruptHandlerReference implements MigolReference {

    private static final InterruptHandlerReference instance = new InterruptHandlerReference();

    private InterruptHandlerReference(){}

    public static InterruptHandlerReference getInstance() {
        return instance;
    }

    public int defer(MigolExecutionSession session) throws MigolExecutionException {
        return session.getHandler();
    }

    public void set(MigolExecutionSession session, int val) throws MigolExecutionException {
        session.setHandler(val);
    }

}
