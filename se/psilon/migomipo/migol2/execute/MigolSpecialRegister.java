package se.psilon.migomipo.migol2.execute;

/**
 *
 * @author John Eriksson
 */
public interface MigolSpecialRegister {

    public int read(MigolExecutionSession session) throws MigolExecutionException;
    public void write(MigolExecutionSession session, int val) throws MigolExecutionException;

}
