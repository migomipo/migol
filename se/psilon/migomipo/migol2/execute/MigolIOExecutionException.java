package se.psilon.migomipo.migol2.execute;

/**
 *  Signals that an I/O error has occured during execution of a Migol program.
 * @author John Eriksson
 */
public class MigolIOExecutionException extends MigolExecutionException {

    
    public MigolIOExecutionException(String mess, int statementnum) {
        super(mess, statementnum);
    }

    
    public MigolIOExecutionException(String mess, Throwable e, int statementnum) {
        super(mess, e, statementnum);
    }
}
