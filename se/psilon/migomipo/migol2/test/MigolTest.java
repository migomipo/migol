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
                "\"i=-4 \"buffer=10 10<-61, 11<-125, -21<handler,-22<buffer,-23<2, -24<2, [-26], [-6], #<3000, i<0:handler")
                .executeProgram(new MigolExecutionSession());
        
     
    }

}
