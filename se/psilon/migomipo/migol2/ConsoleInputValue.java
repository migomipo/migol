package se.psilon.migomipo.migol2;


public class ConsoleInputValue implements WriteValue {

    private static final ConsoleInputValue instance = new ConsoleInputValue();

    public static ConsoleInputValue getInstance() {
        return instance;
    }

    public int defer(MigolExecutionSession session) throws MigolExecutionException {
        try {
            return System.in.read();
        } catch(java.io.IOException ex){
            throw new MigolExecutionException(session.getPP());
        }
    }

    public void set(MigolExecutionSession session, int val) {


    }

}
