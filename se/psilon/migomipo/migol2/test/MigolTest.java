package se.psilon.migomipo.migol2.test;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import se.psilon.migomipo.migol2.MigolParsedProgram;
import se.psilon.migomipo.migol2.MigolExecutionException;
import se.psilon.migomipo.migol2.MigolExecutionSession;
import se.psilon.migomipo.migol2.io.*;
import se.psilon.migomipo.migol2.parse.MigolParser;
import se.psilon.migomipo.migol2.parse.MigolParsingException;

public class MigolTest {

    public static void main(String[] args) throws MigolExecutionException, IOException, MigolParsingException {

        MigolParsedProgram prog = MigolParser.parseFile("E:\\hg\\migol\\timertest.mgl");
        MigolExecutionSession session = new MigolExecutionSession();
        IOManager io = new IOManager();
        FileOperationManager file = new FileOperationManager(io);
        SocketManager soc = new SocketManager(io);
        session.addIOFunction(10, io.getReadStreamFunction());
        session.addIOFunction(11, io.getWriteStreamFunction());
        session.addIOFunction(12, io.getCloseStreamFunction());
        session.addIOFunction(20, file.getOpenFileFunc());
        session.addIOFunction(30, soc.getOpenSocketFunc());
        session.addIOFunction(34, soc.getResolveDNSFunc());
        InterruptTimer t = new InterruptTimer();
        session.addIOFunction(70, t.getCurrentTimeFunction());
        session.addIOFunction(71, t.getScheduleInterruptFunction());
        session.executeProgram(prog);
        io.close();

    }
}
