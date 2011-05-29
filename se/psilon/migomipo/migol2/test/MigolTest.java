package se.psilon.migomipo.migol2.test;


import java.io.IOException;
import se.psilon.migomipo.migol2.MigolParsedProgram;
import se.psilon.migomipo.migol2.MigolExecutionException;
import se.psilon.migomipo.migol2.MigolExecutionSession;
import se.psilon.migomipo.migol2.io.IOManager;
import se.psilon.migomipo.migol2.io.IOUtilities;
import se.psilon.migomipo.migol2.parse.MigolParser;
import se.psilon.migomipo.migol2.parse.MigolParsingException;

public class MigolTest {

    public static void main(String[] args) throws MigolExecutionException, IOException, MigolParsingException {

        MigolParsedProgram prog = MigolParser.parseFile("E:\\hg\\migol\\test.mgl");
                       
        return;

    }
}
