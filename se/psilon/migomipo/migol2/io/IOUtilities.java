package se.psilon.migomipo.migol2.io;

import java.util.logging.Level;
import java.util.logging.Logger;
import se.psilon.migomipo.migol2.MigolExecutionSession;

public class IOUtilities {

    private static final MigolIOFunction instantInterruptFunction = new MigolIOFunction() {

        public void executeIO(MigolExecutionSession session, int structPos) {
            session.getResultQueue().add(structPos);
        }
    };

    public static MigolIOFunction getInstantInterruptFunction() {
        return instantInterruptFunction;
    }
    
    public static void addStdIOFunctions(MigolExecutionSession session, IOManager io){
            session.addIOFunction(5, getInstantInterruptFunction());
            FileOperationManager file = new FileOperationManager(io);
            SocketManager soc = new SocketManager(io);
            session.addIOFunction(10, io.getReadStreamFunction());
            session.addIOFunction(11, io.getWriteStreamFunction());
            session.addIOFunction(12, io.getCloseStreamFunction());
            session.addIOFunction(20, file.getOpenFileFunc());
            session.addIOFunction(24, file.getFileTellFunc());
            session.addIOFunction(25, file.getFileSeekFunc());
            session.addIOFunction(26, file.getFileSizeFunc());
            session.addIOFunction(30, soc.getOpenSocketFunc());
            session.addIOFunction(34, soc.getResolveDNSFunc());
            InterruptTimer time = new InterruptTimer();
            session.addIOFunction(70, time.getCurrentTimeFunction());
            session.addIOFunction(71, time.getScheduleInterruptFunction());
    }
    
            
            
    
}
