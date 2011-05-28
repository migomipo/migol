
package se.psilon.migomipo.migol2.io;

import java.util.Timer;
import java.util.TimerTask;
import se.psilon.migomipo.migol2.MigolExecutionSession;


public class InterruptTimer {
    
    private Timer t = new Timer(true);
    
    
    
    
    private MigolIOFunction scheduleInterruptFunction = new MigolIOFunction() {
        // structpos : function ID
        // structPos + 1: delay
        // structPos + 2: time where the delay occured
        
        public void executeIO(final MigolExecutionSession session, final int structPos) {
            int delay = session.getMemory()[structPos + 1];
            t.schedule(new TimerTask() {

                @Override
                public void run() {
                    session.getMemory()[structPos + 2] = (int) System.currentTimeMillis();
                    session.getResultQueue().add(structPos);
                }
            }, delay);
        }
    };

    public MigolIOFunction getScheduleInterruptFunction() {
        return scheduleInterruptFunction;
    }
    
    private MigolIOFunction currentTimeFunction = new MigolIOFunction() {

        public void executeIO(MigolExecutionSession session, int structPos) {
            int cur = (int) System.currentTimeMillis();
            session.getMemory()[structPos + 1] = cur;
            session.getResultQueue().add(structPos);
        }
    };

    public MigolIOFunction getCurrentTimeFunction() {
        return currentTimeFunction;
    }
    
    
            
    
    
    
    
}
