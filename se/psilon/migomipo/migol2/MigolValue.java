package se.psilon.migomipo.migol2;


public interface MigolValue extends MigolReference {
    public int get(MigolExecutionSession session) throws MigolExecutionException;

}
