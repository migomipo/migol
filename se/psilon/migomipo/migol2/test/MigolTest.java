package se.psilon.migomipo.migol2.test;

import java.io.*;
import se.psilon.migomipo.migol2.execute.MigolExecutionException;
import se.psilon.migomipo.migol2.execute.MigolExecutionSession;
import se.psilon.migomipo.migol2.parse.MigolParser;
import se.psilon.migomipo.migol2.parse.MigolParsingException;

public class MigolTest {
    public static void main(String[] args) throws MigolExecutionException, IOException, MigolParsingException{
        /*System.setIn(new FileInputStream("F:\\hw.malbolge"));
        MigolParsedProgram prog = MigolParser.parseFile("F:\\malbolge.mgl");
        System.out.println("Parsing completed");
        prog.executeProgram(new MigolExecutionSession()); */
        MigolParser.parseFile("E:\\hg\\migol\\switest.mgl").executeProgram(new MigolExecutionSession());
       
     
    }

}
