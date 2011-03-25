/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.psilon.migomipo.migol2;

import se.psilon.migomipo.migol2.execute.MigolExecutionException;
import se.psilon.migomipo.migol2.execute.MigolExecutionSession;

/**
 *
 * @author John
 */
public class ConsoleIOStatement implements MigolStatement {
    
    public int mode;
    public MigolValue val;

    public ConsoleIOStatement(MigolValue val, int mode) {
        this.val = val;
        this.mode = mode;
    }
    
    public void executeStatement(MigolExecutionSession session) throws MigolExecutionException {
        if(mode == 0){
            System.out.print((char) val.fetchValue(session));
        } else if(mode == 1){
            System.out.print(val.fetchValue(session));
        } else throw new IllegalStateException();
    }
  

}
