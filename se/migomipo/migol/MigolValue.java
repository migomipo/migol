package se.migomipo.migol;


public interface MigolValue extends MigolReference {
    public int get(MigolExecutionSession session) throws MigolExecutionException;

}
