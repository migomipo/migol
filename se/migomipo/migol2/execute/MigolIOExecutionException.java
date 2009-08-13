
package se.migomipo.migol2.execute;

/**
 *
 * @author John Eriksson
 */
public class MigolIOExecutionException extends MigolExecutionException {
    public MigolIOExecutionException(){
        super();
    }
    public MigolIOExecutionException(String mess){
        super(mess);
    }

    public MigolIOExecutionException(String mess, Throwable e) {
        super(mess, e);
    }

}
