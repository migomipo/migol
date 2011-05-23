package se.psilon.migomipo.migol2.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Map;
import se.psilon.migomipo.migol2.execute.*;
import java.util.concurrent.*;

public class IOManager {

    private int handlerAddress = 0;
    private int socketHandle = 0;
    private int bufferAddress = 0;
    private int readLength = 0;
    private int allocFd = 20;
    private ExecutorService threadPool;
    private Map<Integer, ByteChannel> map = new ConcurrentHashMap<Integer, ByteChannel>();
    private HandlerAddressRegister handlerAddressRegister = new HandlerAddressRegister();
    private IOHandleRegister ioHandleRegister = new IOHandleRegister();
    private BufferAddressRegister bufferAddressRegister = new BufferAddressRegister();
    private BufferLengthRegister bufferLengthRegister = new BufferLengthRegister();
    private ReadRequestRegister readRequestRegister = new ReadRequestRegister();
    private WriteRequestRegister writeRequestRegister = new WriteRequestRegister();
    private CloseHandleRegister closeHandleRegister = new CloseHandleRegister();
    private InterruptHandleRegister interruptHandleRegister = new InterruptHandleRegister();
    private InterruptBufferAddressRegister interruptBufferAddressRegister = new InterruptBufferAddressRegister();
    private InterruptBufferLengthRegister interruptBufferLengthRegister = new InterruptBufferLengthRegister();
    private InterruptTypeRegister interruptTypeRegister = new InterruptTypeRegister();
    private InterruptErrorRegister errnoRegister = new InterruptErrorRegister();

    public IOManager() {
        threadPool = Executors.newCachedThreadPool();
        map.put(1, new StreamByteChannel(System.in, null));
        map.put(2, new StreamByteChannel(null, System.out));
        map.put(3, new StreamByteChannel(null, System.err));

    }

    public void close() {
        threadPool.shutdown();
    }

    public int getHandlerAddress() {
        return handlerAddress;
    }

    public int addChannel(ByteChannel f) {
        int fd = allocFd++;
        map.put(fd, f);
        return fd;
    }

    public void removeChannel(int pos) {
        map.remove(pos);
    }

    public void submit(Runnable runnable) {
        threadPool.execute(runnable);
    }

    // Boilerplate!
    private class HandlerAddressRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return handlerAddress;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            handlerAddress = val;
        }
    }

    private class IOHandleRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return socketHandle;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            socketHandle = val;
        }
    }

    private class BufferAddressRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return bufferAddress;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            bufferAddress = val;

        }
    }

    private class BufferLengthRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return readLength;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            readLength = val;
        }
    }

    private class ReadRequestRegister implements MigolSpecialRegister {

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
                    if (channel == null) {
                        session.getInterruptQueue().add(new IOInterrupt(
                                bufferAddress, -1, socketHandle, 0, 2));
                        return;
                    }
                    ByteBuffer buf = ByteBuffer.allocate(readLength);
                    int bytes = channel.read(buf);
                    buf.flip();
                    int i = bufferAddress;
                    int[] mem = session.getMemory();
                    while (buf.hasRemaining()) {
                        mem[i++] = buf.get() & 0xFF;
                    }
                    session.getInterruptQueue().add(new IOInterrupt(
                            bufferAddress, bytes, socketHandle, 0, 0));

                } catch (IOException ex) {
                    int errornumber = 1;
                    session.getInterruptQueue().add(new IOInterrupt(
                            bufferAddress, -1, socketHandle, 0, errornumber));
                }
            }
        }

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            ByteChannel channel = map.get(socketHandle);
            threadPool.execute(new ReadRequest(channel, session, bufferAddress, readLength, socketHandle));
            return 1;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    private class InterruptHandleRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            MigolInterrupt curInterrupt = session.getCurInterrupt();
            if (curInterrupt != null && curInterrupt instanceof IOInterrupt) {
                return ((IOInterrupt) curInterrupt).getIOHandle();
            } else {
                return 0;
            }
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    private class InterruptBufferAddressRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            MigolInterrupt curInterrupt = session.getCurInterrupt();
            if (curInterrupt != null && curInterrupt instanceof IOInterrupt) {
                return ((IOInterrupt) curInterrupt).getBufferAddress();
            } else {
                return 0;
            }
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    private class InterruptBufferLengthRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            MigolInterrupt curInterrupt = session.getCurInterrupt();
            if (curInterrupt != null && curInterrupt instanceof IOInterrupt) {
                return ((IOInterrupt) curInterrupt).getBytes();
            } else {
                return 0;
            }
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    private class InterruptErrorRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            MigolInterrupt curInterrupt = session.getCurInterrupt();
            if (curInterrupt != null && curInterrupt instanceof IOInterrupt) {
                return ((IOInterrupt) curInterrupt).getError();
            } else {
                return 0;
            }
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    private class InterruptTypeRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            MigolInterrupt curInterrupt = session.getCurInterrupt();
            if (curInterrupt != null && curInterrupt instanceof IOInterrupt) {
                return ((IOInterrupt) curInterrupt).getType();
            } else {
                return 0;
            }
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    private class WriteRequestRegister implements MigolSpecialRegister {

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
                    if (channel == null) {
                        session.getInterruptQueue().add(new IOInterrupt(
                                bufferAddress, -1, socketHandle, 1, 2));
                        return;
                    }
                    int[] mem = session.getMemory();
                    ByteBuffer buf = ByteBuffer.allocate(bufferLength);
                    for (int i = 0; i < bufferLength; i++) {
                        buf.put((byte) mem[bufferAddress + i]);
                    }
                    buf.flip();
                    int bytes = channel.write(buf);
                    session.getInterruptQueue().add(new IOInterrupt(
                             bufferAddress, bytes, socketHandle, 1, 0));
                } catch (IOException ex) {
                    int errornumber = 1;
                    session.getInterruptQueue().add(new IOInterrupt(
                            bufferAddress, -1, socketHandle, 1, errornumber));
                }
            }
        }

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            ByteChannel channel = map.get(socketHandle);
            threadPool.execute(new WriteRequest(channel, session, bufferAddress, readLength));
            return 1;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    private class CloseHandleRegister implements MigolSpecialRegister {

        public CloseHandleRegister() {
        }

        public int read(final MigolExecutionSession session) throws MigolExecutionException {
            final ByteChannel channel = map.get(socketHandle);
            threadPool.execute(new Runnable() {

                public void run() {
                    try {
                        channel.close();
                        removeChannel(socketHandle);
                        session.getInterruptQueue().add(
                                new IOInterrupt(0, 0, socketHandle, 2, 0));
                    } catch (IOException ex) {
                        int errornumber = 1;
                        session.getInterruptQueue().add(
                                new IOInterrupt(0, 0, socketHandle, 2, errornumber));
                    }
                }
            });
            return 1;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    private class IOInterrupt implements MigolInterrupt {

        private final int bufferAddress;
        private final int bytes;
        private final int socketHandle;
        private final int type;
        private final int error;

        public IOInterrupt(int bufferAddress, int bytes, int socketHandle, int type, int error) {
 
   
            this.bufferAddress = bufferAddress;
            this.bytes = bytes;
            this.socketHandle = socketHandle;
            this.type = type;
            this.error = error;
        }

        public int getBufferAddress() {
            return bufferAddress;
        }

        public int getBytes() {
            return bytes;
        }

        public int getIOHandle() {
            return socketHandle;
        }

        public int getType() {
            return type;
        }

        public int getError() {
            return error;
        }

        public int getHandlerAddress(MigolExecutionSession session) {
            return IOManager.this.getHandlerAddress();
        }
    }

    public MigolSpecialRegister getBufferAddressRegister() {
        return bufferAddressRegister;
    }

    public MigolSpecialRegister getBufferLengthRegister() {
        return bufferLengthRegister;
    }

    public MigolSpecialRegister getHandlerAddressRegister() {
        return handlerAddressRegister;
    }

    public MigolSpecialRegister getInterruptBufferAddressRegister() {
        return interruptBufferAddressRegister;
    }

    public MigolSpecialRegister getInterruptBufferLengthRegister() {
        return interruptBufferLengthRegister;
    }

    public MigolSpecialRegister getInterruptHandleRegister() {
        return interruptHandleRegister;
    }

    public MigolSpecialRegister getInterruptTypeRegister() {
        return interruptTypeRegister;
    }

    public MigolSpecialRegister getIoHandleRegister() {
        return ioHandleRegister;
    }

    public MigolSpecialRegister getReadRequestRegister() {
        return readRequestRegister;
    }

    public MigolSpecialRegister getWriteRequestRegister() {
        return writeRequestRegister;
    }

    public MigolSpecialRegister getCloseHandleRegister() {
        return closeHandleRegister;
    }

    public MigolSpecialRegister getIOErrnoRegister() {
        return errnoRegister;
    }

    public ByteChannel getChannel(int i) {
        return map.get(i);
    }

    public ByteChannel getCurrentChannel() {
        return getChannel(socketHandle);
    }
    // May be temporary, multithreading and streams isn't the most efficient
    // way to do I/O in Java
}
