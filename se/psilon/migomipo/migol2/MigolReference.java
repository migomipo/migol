package se.psilon.migomipo.migol2;

public interface MigolReference {
    public int defer(MigolExecutionSession session) throws MigolExecutionException;
    public void set(MigolExecutionSession session, int val) throws MigolExecutionException;

}