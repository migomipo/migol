package se.psilon.migomipo.migol2.test;

import java.io.*;
import se.psilon.migomipo.migol2.execute.MigolExecutionException;
import se.psilon.migomipo.migol2.execute.MigolExecutionSession;
import se.psilon.migomipo.migol2.parse.MigolParser;
import se.psilon.migomipo.migol2.parse.MigolParsingException;

public class MigolTest {
    public static void main(String[] args) throws MigolExecutionException, IOException, MigolParsingException{
        String code = "-18<6, -17<500, 'R>, W<[W], #<$-2, [0]>-, 0<$+1, 10>, I<0";
        MigolExecutionSession session = new MigolExecutionSession();
       
        session.executeProgram(MigolParser.parseString(code));

        
        
        
     
    }

}
