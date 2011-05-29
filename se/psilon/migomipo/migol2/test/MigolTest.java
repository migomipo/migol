package se.psilon.migomipo.migol2.test;


import java.io.IOException;
import se.psilon.migomipo.migol2.MigolParsedProgram;
import se.psilon.migomipo.migol2.MigolExecutionException;
import se.psilon.migomipo.migol2.parse.MigolParser;
import se.psilon.migomipo.migol2.parse.MigolParsingException;

public class MigolTest {

    public static void main(String[] args) throws MigolExecutionException, IOException, MigolParsingException {

        MigolParsedProgram prog = MigolParser.parseString("3<4, 6<4");
        
        return;

    }
}
