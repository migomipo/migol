package se.psilon.migomipo.migol2.test;

import java.io.*;
import se.psilon.migomipo.migol2.MigolParsedProgram;
import se.psilon.migomipo.migol2.execute.MigolExecutionException;
import se.psilon.migomipo.migol2.execute.MigolExecutionSession;
import se.psilon.migomipo.migol2.parse.MigolParser;
import se.psilon.migomipo.migol2.parse.MigolParsingException;

public class MigolTest {
    public static void main(String[] args) throws MigolExecutionException, IOException, MigolParsingException{
        //String code = "-21<9,-22<10,-23<10, -24<1, -6<[-25], -6<[-6], 'E>, #<3000, 'I>, I<0";
        //String code = "-12<6, -11<500, 'R>, W<[W], #<$-2, [0]>-, 0<$+1, 10>, I<0";
        
        MigolParser.parseString(
                "\"I=-4 \"BUFFER=10 -21<HANDLER,-22<BUFFER,-23<1, -24<2, BUFFER<'J, [-26], [-6], 'E>, [-28]>-, #<3000, 'I>:HANDLER, [-28]>-, I<0")
                .executeProgram(new MigolExecutionSession());
        
     
    }

}
