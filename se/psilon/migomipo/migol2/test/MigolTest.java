/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.psilon.migomipo.migol2.test;

import java.io.FileInputStream;
import java.io.IOException;
import se.psilon.migomipo.migol2.MigolParsedProgram;
import se.psilon.migomipo.migol2.MigolExecutionException;
import se.psilon.migomipo.migol2.MigolExecutionSession;
import se.psilon.migomipo.migol2.parse.MigolParser;
import se.psilon.migomipo.migol2.parse.MigolParsingException;

/**
 *
 * @author joheri01
 */
public class MigolTest {

    public static void main(String[] args) throws MigolExecutionException, IOException, MigolParsingException {
        System.setIn(new FileInputStream("E:\\hw.malbolge"));
        MigolParsedProgram prog = MigolParser.parseFile("E:\\malbolge.mgl");
        System.out.println("Parsing completed");
        prog.executeProgram(new MigolExecutionSession());

}
}
