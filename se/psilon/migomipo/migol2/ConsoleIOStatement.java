/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.psilon.migomipo.migol2;

/**
 *
 * @author John
 */
public class ConsoleIOStatement implements MigolStatement {
    
    public int mode;
    public ReadValue val;

    public ConsoleIOStatement(ReadValue val, int mode) {
        this.val = val;
        this.mode = mode;
    }
    
    public void executeStatement(MigolExecutionSession session) throws MigolExecutionException {
        if(mode == 0){
            System.out.write(val.get(session));
            System.out.flush();
        } else if(mode == 1){
            System.out.print(val.get(session));
        } else throw new IllegalStateException();
    }
  

}
