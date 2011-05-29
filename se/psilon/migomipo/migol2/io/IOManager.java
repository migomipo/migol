package se.psilon.migomipo.migol2.io;

import se.psilon.migomipo.migol2.MigolExecutionSession;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Map;
import java.util.concurrent.*;

public class IOManager {

    private int allocFd = 20;
    private ExecutorService threadPool;
    private Map<Integer, Object> map = new ConcurrentHashMap<Integer, Object>();

    public IOManager() {
        threadPool = Executors.newCachedThreadPool();
        map.put(1, new StreamByteChannel(System.in, null));
        map.put(2, new StreamByteChannel(null, System.out));
        map.put(3, new StreamByteChannel(null, System.err));

    }

    public void close() {
        threadPool.shutdown();
    }

    public int addObject(Object o) {
        int fd = allocFd++;
        map.put(fd, o);
        return fd;
    }

    public void removeChannel(int pos) {
        map.remove(pos);
    }

    public void submit(Runnable runnable) {
        threadPool.execute(runnable);
    }

    private class ReadRequest implements Runnable {

        // structPos    :  function id
        // structPos + 1:  stream handle
        // structPos + 2:  buffer address
        // structPos + 3:  buffer length
        // structPos + 4:  error num
        // structPos + 5:  bytes read

        private final int structPos;
        private final MigolExecutionSession session;

        private ReadRequest(MigolExecutionSession session, int structPos) {
            this.structPos = structPos;
            this.session = session;
        }

        public void run() {
            int error = 0;
            int bytes = -1;
            int[] mem = session.getMemory();
            try {
                ByteChannel channel = (ByteChannel) map.get(mem[structPos + 1]);

                if (channel == null) {
                    throw new NullPointerException();
                }
                ByteBuffer buf = ByteBuffer.allocate(mem[structPos + 3]);
                bytes = channel.read(buf);
                buf.flip();
                int i = mem[structPos + 2];
                while (buf.hasRemaining()) {
                    mem[i++] = buf.get() & 0xFF;
                }

            } catch (IOException ex) {
                error = 1;
            } catch (NullPointerException ex) {                
                error = 2;
            } catch(IllegalArgumentException ex){
                error = 3;
            } catch(ClassCastException ex){
                error = 4;
            }
            mem[structPos + 4] = error;
            mem[structPos + 5] = bytes;
            session.getResultQueue().add(structPos);
        }
    }

    private class WriteRequest implements Runnable {

        // structPos    :  function id
        // structPos + 1:  stream handle
        // structPos + 2:  buffer address
        // structPos + 3:  buffer length
        // structPos + 4:  error num
        // structPos + 5:  bytes written

        private final MigolExecutionSession session;
        private final int structPos;

        private WriteRequest(MigolExecutionSession session, int structPos) {
            this.session = session;
            this.structPos = structPos;
        }

        public void run() {
            int bytes = -1;
            int error = 0;
            int[] mem = session.getMemory();
            try {
                ByteChannel channel = (ByteChannel) map.get(mem[structPos + 1]);
                if (channel == null) {
                    throw new NullPointerException();
                }
                int len = mem[structPos + 3];
                ByteBuffer buf = ByteBuffer.allocate(len);
                int pos = mem[structPos + 2];
                for (int i = 0; i < len; i++) {
                    buf.put((byte) mem[pos++]);
                }
                buf.flip();
                bytes = channel.write(buf);

            } catch (IOException ex) {
                error = 1;

            } catch (NullPointerException ex) {
                error = 2;
            } catch(IllegalArgumentException ex){
                error = 3;
            } catch(ClassCastException ex){
                error = 4;
            }
            
            mem[structPos + 4] = error;
            mem[structPos + 5] = bytes;
            session.getResultQueue().add(structPos);
        }
    }

    private class CloseRequest implements Runnable {

        // structPos    :  function id
        // structPos + 1:  stream handle
        // structPos + 2:  error num

        private final int structPos;
        private final MigolExecutionSession session;

        private CloseRequest(MigolExecutionSession session, int structPos) {
            this.structPos = structPos;
            this.session = session;
        }

        public void run() {
            int error = 0;
            int[] mem = session.getMemory();
            try {
                ByteChannel channel = (ByteChannel) map.get(mem[structPos + 1]);
                if (channel == null) {
                    throw new NullPointerException();
                }
                channel.close();
            } catch (IOException ex) {
                error = 1;
            } catch (NullPointerException ex) {
                error = 2;
            } catch(ClassCastException ex){
                error = 4;
            }
            mem[structPos + 2] = error;
            session.getResultQueue().add(structPos);

        }
    }

    public Object getObject(int i) {
       return map.get(i);
        
    }
    
    private MigolIOFunction readStreamFunction = new MigolIOFunction() {

        public void executeIO(MigolExecutionSession session, int structPos) {
            submit(new ReadRequest(session, structPos));
        }
    };
    private MigolIOFunction writeStreamFunction = new MigolIOFunction() {

        public void executeIO(MigolExecutionSession session, int structPos) {
            submit(new WriteRequest(session, structPos));
        }
    };
    private MigolIOFunction closeStreamFunction = new MigolIOFunction() {

        public void executeIO(MigolExecutionSession session, int structPos) {
            submit(new CloseRequest(session, structPos));
        }
    };

    public MigolIOFunction getCloseStreamFunction() {
        return closeStreamFunction;
    }

    public MigolIOFunction getReadStreamFunction() {
        return readStreamFunction;
    }

    public MigolIOFunction getWriteStreamFunction() {
        return writeStreamFunction;
    }
    
    public Map<Integer, Object> getObjectMap(){
        return map;
    }


    // May be temporary, multithreading and streams isn't the most efficient
    // way to do I/O in Java
}
