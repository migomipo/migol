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

        private int interruptTime;

        public TimerInterrupt(int interruptTime) {
            this.interruptTime = interruptTime;
        }

        public int getHandlerAddress(MigolExecutionSession session) {
            return TimerInterruptManager.this.handlerAddress;
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
                        session.getInterruptQueue().add(new TimerInterrupt(
                                (int) System.currentTimeMillis()));
                    }
                }, time, time);

            }
        }
    }

    public class TimerInterruptTimeRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            MigolInterrupt curInterrupt = session.getCurInterrupt();
            if (curInterrupt instanceof TimerInterrupt) {
                return ((TimerInterrupt) curInterrupt).interruptTime;
            } else {
                return 0;
            }
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public class MilliClockRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return (int) System.currentTimeMillis();
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }
}
