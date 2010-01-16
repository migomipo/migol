/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.psilon.migomipo.migol2.test;
import se.psilon.migomipo.migol2.*;
import se.psilon.migomipo.migol2.parse.*;
import se.psilon.migomipo.migol2.execute.*;
/**
 *
 * @author john
 */
public class MigolTest {
    public static void main(String[] args) throws Throwable{
        MigolParsedProgram program = MigolParser.parseString("0<100000000, 0<$-1, -1<2?<>[0]");
        final MigolExecutionSession session = new MigolExecutionSession();        
        program.executeProgram(session);
        int[] memory = session.getMemory();
        for(int i = 0;i<10;i++){
            System.out.println(i + " : " + memory[i]);
        }

    }

}
