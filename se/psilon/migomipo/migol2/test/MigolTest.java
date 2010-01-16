package se.psilon.migomipo.migol2.test;
import se.psilon.migomipo.migol2.*;
import se.psilon.migomipo.migol2.parse.*;
import se.psilon.migomipo.migol2.execute.*;
/**
 *
 * @author John Eriksson
 */
public class MigolTest {
    public static void main(String[] args) throws Throwable{
        MigolParsedProgram program = MigolParser.parseString("0<3, _, 0<4, 1<7");
        final MigolExecutionSession session = new MigolExecutionSession(); 
        program.executeProgram(session);
        int[] memory = session.getMemory();
        for(int i = 0;i<10;i++){
            System.out.println(i + " : " + memory[i]);
        }

    }

}
