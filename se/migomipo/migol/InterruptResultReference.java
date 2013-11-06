package se.migomipo.migol;


public class InterruptResultReference implements MigolReference {

    private static final InterruptResultReference instance = new InterruptResultReference();

    private InterruptResultReference(){}


    public static InterruptResultReference getInstance() {
        return instance;
    }

    public int defer(MigolExecutionSession session) throws MigolExecutionException {
        return session.getResult();
    }

    public void set(MigolExecutionSession session, int val) throws MigolExecutionException {

    }

}
