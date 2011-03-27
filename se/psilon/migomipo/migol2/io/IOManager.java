package se.psilon.migomipo.migol2.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import se.psilon.migomipo.migol2.execute.MigolExecutionException;
import se.psilon.migomipo.migol2.execute.MigolExecutionSession;
import se.psilon.migomipo.migol2.execute.MigolSpecialRegister;
import java.util.concurrent.*;

public class IOManager {

    private int handlerAddress = 0;
    private int socketHandle = 0;
    private int bufferAddress = 0;
    private int readLength = 0;
    private ExecutorService threadPool;
    private ConcurrentHashMap<Integer, ByteChannel> map = new ConcurrentHashMap<Integer, ByteChannel>();

    public IOManager() {
        threadPool = Executors.newCachedThreadPool();
        map.put(1, new StreamByteChannel(System.in, null));
        map.put(2, new StreamByteChannel(null, System.out));
        map.put(3, new StreamByteChannel(null, System.err));

    }
    
    public void close(){
        threadPool.shutdown();
    }
    
    // Boilerplate!
    public class HandlerAddressRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return handlerAddress;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            handlerAddress = val;
        }
    }

    public class IOHandleRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return socketHandle;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            socketHandle = val;
        }
    }

    public class BufferAddressRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return bufferAddress;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            bufferAddress = val;

        }
    }
    
    public class BufferLengthRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return readLength;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            readLength = val;
        }
    
    }

    public class ReadRequestRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            ByteChannel channel = map.get(socketHandle);
            threadPool.execute(new ReadRequest(channel, session, bufferAddress, readLength, socketHandle));
            return 1;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    public class WriteRequestRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            ByteChannel channel = map.get(socketHandle);
            threadPool.execute(new WriteRequest(channel, session, bufferAddress, readLength));
            return 1;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    // May be temporary, multithreading and streams aren't the most efficient
    // way to do I/O in Java
    private class ReadRequest implements Runnable {

        private final ByteChannel channel;
        private final int bufferAddress;
        private final int readLength;
        private final MigolExecutionSession session;
        private final int socketHandle;

        private ReadRequest(ByteChannel channel, MigolExecutionSession session, int bufferAddress, int readLength, int socketHandle) {
            this.channel = channel;
            this.bufferAddress = bufferAddress;
            this.readLength = readLength;
            this.session = session;
            this.socketHandle = socketHandle;
        }

        public void run() {
            try {
                ByteBuffer buf = ByteBuffer.allocate(readLength);
                int bytes = channel.read(buf);
                buf.flip();
                int i = bufferAddress;
                int[] mem = session.getMemory();
                while (buf.hasRemaining()) {
                    mem[i++] = buf.get() & 0xFF;
                } 
                session.getInterruptQueue().add(new IOInterrupt(bufferAddress, bytes, socketHandle, 0));

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class WriteRequest implements Runnable {

        private final ByteChannel channel;
        private final int bufferAddress;
        private final int bufferLength;
        private final MigolExecutionSession session;

        private WriteRequest(ByteChannel channel, MigolExecutionSession session, int bufferAddress, int writeLength) {
            this.channel = channel;
            this.bufferAddress = bufferAddress;
            this.bufferLength = writeLength;
            this.session = session;
        }

        public void run() {
            try {
                int[] mem = session.getMemory();
                ByteBuffer buf = ByteBuffer.allocate(bufferLength);
                for (int i = 0; i < bufferLength; i++) {
                    buf.put((byte) mem[bufferAddress + i]);
                }
                buf.flip();
                int bytes = channel.write(buf);
                session.getInterruptQueue().add(new IOInterrupt(bufferAddress, bytes, socketHandle, 1));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private class IOInterrupt implements MigolInterrupt {
        private final int bufferAddress;
        private final int bytes;
        private final int socketHandle;
        private final int type;

        private IOInterrupt(int bufferAddress, int bytes, int socketHandle, int type) {
            this.bufferAddress = bufferAddress;
            this.bytes = bytes;
            this.socketHandle = socketHandle;
            this.type = type;
        }

        public void enter(MigolExecutionSession session) {            
            session.doInterrupt(IOManager.this.handlerAddress);
        }

        public void exit(MigolExecutionSession session) {
            
        }
    }

    
    
}
