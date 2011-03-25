/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.psilon.migomipo.migol2.execute;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author joheri01
 */
class TimerInterruptManager {

    private int handlerAddress;
    private int time;
    private Timer timer;
    private MigolSpecialRegister handlerRegister, configRegister;

    public TimerInterruptManager() {
        handlerAddress = 0;
        time = 0;
        timer = null;
        handlerRegister = new TimerInterruptHandlerRegister();
        configRegister = new TimerInterruptConfigRegister();
    }

    public MigolSpecialRegister getHandlerAddressRegister() {
        return handlerRegister;
    }

    public MigolSpecialRegister getTimerConfigRegister() {
        return configRegister;
    }

    private class TimerInterrupt implements MigolInterrupt {

        public void execute(MigolExecutionSession session) {
            session.doInterrupt(TimerInterruptManager.this.handlerAddress);
        }
    }

    private class TimerInterruptHandlerRegister implements MigolSpecialRegister {

        public TimerInterruptHandlerRegister() {
        }

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return TimerInterruptManager.this.handlerAddress;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            TimerInterruptManager.this.handlerAddress = val;
        }
    }

    private class TimerInterruptConfigRegister implements MigolSpecialRegister {

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

