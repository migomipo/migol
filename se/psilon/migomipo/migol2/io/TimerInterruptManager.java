
package se.psilon.migomipo.migol2.io;

import java.util.Timer;
import java.util.TimerTask;
import se.psilon.migomipo.migol2.execute.MigolExecutionException;
import se.psilon.migomipo.migol2.execute.MigolExecutionSession;
import se.psilon.migomipo.migol2.execute.MigolSpecialRegister;

public class TimerInterruptManager {

    private int handlerAddress;
    private int time;
    private Timer timer;
    
    public TimerInterruptManager() {
        handlerAddress = 0;
        time = 0;
        timer = null;

    }

    private class TimerInterrupt implements MigolInterrupt {

        public void enter(MigolExecutionSession session) {
            session.doInterrupt(TimerInterruptManager.this.handlerAddress);
        }

        public void exit(MigolExecutionSession session) {
            // NOTHING
        }
        
    }

    public class TimerInterruptHandlerRegister implements MigolSpecialRegister {

        public TimerInterruptHandlerRegister() {
        }

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return TimerInterruptManager.this.handlerAddress;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            TimerInterruptManager.this.handlerAddress = val;
        }
    }

    public class TimerInterruptConfigRegister implements MigolSpecialRegister {

        public TimerInterruptConfigRegister() {
        }

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return TimerInterruptManager.this.time;
        }

        public void write(final MigolExecutionSession session, int val) throws MigolExecutionException {
            time = Math.max(0, val);
            if (timer != null) {
                timer.cancel();
            }
            if (time == 0) {
                timer = null;
            } else {
                timer = new Timer(true);
                timer.scheduleAtFixedRate(new TimerTask() {

                    @Override
                    public void run() {
                        session.getInterruptQueue().add(new TimerInterrupt());
                    }
                }, time, time);

            }
        }
    }
}

