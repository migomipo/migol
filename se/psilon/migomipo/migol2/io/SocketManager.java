package se.psilon.migomipo.migol2.io;

import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;
import se.psilon.migomipo.migol2.execute.MigolExecutionSession;


public class SocketManager {

    private IOManager io;

    public SocketManager(IOManager io) {
        this.io = io;
    }
  
    private class OpenSocketRequest implements Runnable {

        private final int structPos;
        private final MigolExecutionSession session;

        private OpenSocketRequest(MigolExecutionSession session, int structPos) {
            this.structPos = structPos;
            this.session = session;
        }

        public void run() {
            int[] mem = session.getMemory();
            int error = 0;
            int handle = -1;
            try {
                byte[] ip;
                int ipaddresstype = mem[structPos];
                int addresspos = mem[structPos + 1];
                int port = mem[structPos + 2];
                if (ipaddresstype == 0) {
                    ip = new byte[4];
                    for (int i = 0; i < 4; i++) {
                        ip[i] = (byte) mem[addresspos + i];
                    }
                } else if (ipaddresstype == 1) {
                    ip = new byte[16];
                    for (int i = 0; i < 16; i++) {
                        ip[i] = (byte) mem[addresspos + i];
                    }
                } else {
                    throw new IllegalArgumentException();
                }
                SocketChannel ch = SocketChannel.open(new InetSocketAddress(InetAddress.getByAddress(ip), port));
                handle = io.addChannel(ch);
                
            } catch (IOException ex) {
                error = 1;                
            } catch (IllegalArgumentException ex){
                error = 2;
            }
            mem[structPos + 4] = error;
            mem[structPos + 5] = handle;
            session.getResultQueue().add(structPos);
        }
        
    }

    private MigolIOFunction openSocketFunc = new MigolIOFunction() {

        public void executeIO(MigolExecutionSession session, int structPos) {
            io.submit(new OpenSocketRequest(session, structPos));
        }
    };

    public MigolIOFunction getOpenSocketFunc() {
        return openSocketFunc;
    }

    
}
