package se.psilon.migomipo.migol2;


public class ConsoleInputReference implements MigolReference {

    private static final ConsoleInputReference instance = new ConsoleInputReference();

    private ConsoleInputReference(){}

    public static ConsoleInputReference getInstance() {
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
