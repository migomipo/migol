package se.psilon.migomipo.migol2;


public class InterruptResultValue implements WriteValue {

    private static final InterruptResultValue instance = new InterruptResultValue();

    private InterruptResultValue(){}


    public static InterruptResultValue getInstance() {
        return instance;
    }

    public int defer(MigolExecutionSession session) throws MigolExecutionException {
        return session.getResult();
    }

    public void set(MigolExecutionSession session, int val) throws MigolExecutionException {

    }

}
