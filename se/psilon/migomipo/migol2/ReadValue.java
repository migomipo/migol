package se.psilon.migomipo.migol2;


public interface ReadValue extends WriteValue {
    public int get(MigolExecutionSession session) throws MigolExecutionException;

}
