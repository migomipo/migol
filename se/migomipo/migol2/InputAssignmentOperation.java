package se.migomipo.migol2;

import java.io.IOException;
import se.migomipo.migol2.execute.*;


/**
 *
 * @author John Eriksson
 */
public class InputAssignmentOperation implements AssignmentOperation {
    private static InputAssignmentOperation op = new InputAssignmentOperation();
    
    public static AssignmentOperation getInstance() {
        return op;
    }
    
    public int operation(MigolExecutionSession session, int currentvalue) throws MigolExecutionException {
        // Current value is ignored
        MigolIOCallback callback = session.getIOCallback();
        try {
            return callback.inputValue();
        } catch(IOException e){
            throw new MigolIOExecutionException("I/O operation failed at statement " + session.getPP(),e,session.getPP());
        }
    }

    public String toMigolSyntax() {
        return "<@";
    }
    

}
