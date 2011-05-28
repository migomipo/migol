package se.psilon.migomipo.migol2.io;

import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;
import se.psilon.migomipo.migol2.MigolExecutionSession;


public class SocketManager {

    private IOManager io;

    public SocketManager(IOManager io) {
        this.io = io;
    }
  
    private class OpenSocketRequest implements Runnable {

        // structpos: function ID
        // structpos + 1: address type
        // structpos + 2: ip address address
        // structpos + 3: port
        // structpos + 4: error
        // structpos + 5: handle

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
                int ipaddresstype = mem[structPos + 1];
                int addresspos = mem[structPos + 2];
                int port = mem[structPos + 3];
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
                error = 3;
            }
            mem[structPos + 4] = error;
            mem[structPos + 5] = handle;
            session.getResultQueue().add(structPos);
        }
        
    }
    
    private class ResolveDNSRequest implements Runnable {
        
        // structpos: function ID
        // structpos + 1: DNS name string address
        // structpos + 2: DNS name string length
        // structpos + 3: IP address save position
        // structpos + 4: error
        // structpos + 5: IP address type (0=ipv4, 1=ipv6)
        
        private final int structPos;
        private final MigolExecutionSession session;

        private ResolveDNSRequest(MigolExecutionSession session, int structPos) {
            this.structPos = structPos;
            this.session = session;
        }

        public void run() {
            int mode = -1;
            int error = 0;
            
            int[] mem = session.getMemory();     
            try {
                int namelen = mem[structPos + 2];
                char[] name = new char[namelen];
                int charpos = mem[structPos + 1];
                for(int i=0;i<namelen;i++){
                    name[i] = (char) mem[charpos + i];
                }
                InetAddress addr = InetAddress.getByName(new String(name));
                byte[] addrbytes = addr.getAddress();
                int savepos = mem[structPos + 3];
                for(int i=0;i<addrbytes.length;i++){
                    mem[savepos + i] = addrbytes[i];
                }
                if(addrbytes.length == 4){
                    mode = 0;
                } else if(addrbytes.length == 16){
                    mode = 1;
                }
            } catch(UnknownHostException ex){
                error = 1;
            }
            mem[structPos + 4] = error;
            mem[structPos + 5] = error;
            session.getResultQueue().add(structPos);
            
        }
    
    }

    private MigolIOFunction openSocketFunc = new MigolIOFunction() {

        public void executeIO(MigolExecutionSession session, int structPos) {
            io.submit(new OpenSocketRequest(session, structPos));
        }
    };
    
    private MigolIOFunction resolveDNSFunc = new MigolIOFunction() {

        public void executeIO(MigolExecutionSession session, int structPos) {
            io.submit(new ResolveDNSRequest(session, structPos));
        }
    };

    public MigolIOFunction getOpenSocketFunc() {
        return openSocketFunc;
    }

    public MigolIOFunction getResolveDNSFunc() {
        return resolveDNSFunc;
    }
   
}
