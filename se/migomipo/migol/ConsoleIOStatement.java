/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.migomipo.migol;

/**
 *
 * @author John
 */
public class ConsoleIOStatement implements MigolStatement {
    
    public static final int ASCII = 0;
    public static final int INT = 1;
    
    public int mode;
    public MigolValue val;

    public ConsoleIOStatement(MigolValue val, int mode) {
        this.val = val;
        this.mode = mode;
    }
    
    public void executeStatement(MigolExecutionSession session) throws MigolExecutionException {
        if(mode == ASCII){
            System.out.write(val.get(session));
            System.out.flush();
        } else if(mode == INT){
            System.out.print(val.get(session));
        } else throw new IllegalStateException();
    }

    public int getMode() {
        return mode;
    }

    public MigolValue getVal() {
        return val;
    }
    
    
  

}
