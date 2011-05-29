package se.psilon.migomipo.migol2.io;

import se.psilon.migomipo.migol2.MigolExecutionSession;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.*;

public class FileOperationManager {

    private IOManager io;

    public FileOperationManager(IOManager io) {
        this.io = io;
    }

    private class FileOpenRequest implements Runnable {

        // structpos : Function ID
        // structpos + 1: file name position
        // structpos + 2: file name length
        // structpos + 3: mode
        private MigolExecutionSession session;
        private int structPos;

        public FileOpenRequest(MigolExecutionSession session, int structPos) {
            this.session = session;
            this.structPos = structPos;
        }

        public void run() {
            int error = 0;
            int handle = -1;
            int[] mem = session.getMemory();
            int filenamelen = mem[structPos + 2];
            int filenamepos = mem[structPos + 1];
            byte[] bytes = new byte[filenamelen];

            for (int i = 0; i < filenamelen; i++) {
                bytes[i] = (byte) mem[filenamepos + i];
            }
            try {
                int filemode = mem[structPos + 3];
                String mode;
                if (filemode == 0) {
                    mode = "r";
                } else if (filemode == 1) {
                    mode = "rw";
                } else {
                    throw new IllegalArgumentException();
                }
                FileChannel f = new RandomAccessFile(new String(bytes, "utf-8"), mode).getChannel();
                handle = io.addObject(f);

            } catch (IOException ex) {
                error = 1;
            } catch (IllegalArgumentException ex) {
                error = 2;
            } catch (NegativeArraySizeException ex) {
                error = 3;
            }
            mem[structPos + 4] = error;
            mem[structPos + 5] = handle;
            session.getResultQueue().add(structPos);
        }
    }

    private class FileTellRequest implements Runnable {

        private MigolExecutionSession session;
        private int structPos;

        public FileTellRequest(MigolExecutionSession session, int structPos) {
            this.session = session;
            this.structPos = structPos;
        }

        // structPos : function ID
        // structPos + 1: file handle
        // structPos + 2: error num
        // structPos + 3: file position
        public void run() {
            int error = 0;
            int fileposition = -1;
            int[] mem = session.getMemory();
            try {
                FileChannel fc = (FileChannel) io.getObject(mem[structPos + 1]);
                fileposition = (int) fc.position();

            } catch (ClassCastException ex) {
                error = 4;
            } catch (IOException ex) {
                error = 1;
            } catch (NullPointerException ex) {
                error = 2;
            }
            mem[structPos + 2] = error;
            mem[structPos + 3] = fileposition;
            session.getResultQueue().add(structPos);

        }
    }

    private class FileSeekRequest implements Runnable {

        private MigolExecutionSession session;
        private int structPos;

        public FileSeekRequest(MigolExecutionSession session, int structPos) {
            this.session = session;
            this.structPos = structPos;
        }

        // structPos : function ID
        // structPos + 1: file handle
        // structPos + 2: new position
        // structPos + 3: error num    
        public void run() {
            int error = 0;
            int[] mem = session.getMemory();
            try {
                FileChannel fc = (FileChannel) io.getObject(mem[structPos + 1]);
                fc.position(mem[structPos + 2]);

            } catch (ClassCastException ex) {
                error = 4;
            } catch (IOException ex) {
                error = 1;
            } catch (NullPointerException ex) {
                error = 2;
            }
            mem[structPos + 3] = error;
            session.getResultQueue().add(structPos);

        }
    }

    private class FileSizeRequest implements Runnable {

        private MigolExecutionSession session;
        private int structPos;

        public FileSizeRequest(MigolExecutionSession session, int structPos) {
            this.session = session;
            this.structPos = structPos;
        }

        // structPos : function ID
        // structPos + 1: file handle
        // structPos + 2: error num
        // structPos + 3: file size    
        public void run() {
            int error = 0;
            int size = -1;
            int[] mem = session.getMemory();
            try {
                FileChannel fc = (FileChannel) io.getObject(mem[structPos + 1]);
                size = (int) fc.size();

            } catch (ClassCastException ex) {
                error = 4;
            } catch (IOException ex) {
                error = 1;
            } catch (NullPointerException ex) {
                error = 2;
            }
            mem[structPos + 2] = error;
            mem[structPos + 3] = size;
            session.getResultQueue().add(structPos);

        }
    }
    private MigolIOFunction openFileFunc = new MigolIOFunction() {

        public void executeIO(MigolExecutionSession session, int structPos) {
            io.submit(new FileOpenRequest(session, structPos));
        }
    };
    private MigolIOFunction fileTellFunc = new MigolIOFunction() {

        public void executeIO(MigolExecutionSession session, int structPos) {
            io.submit(new FileTellRequest(session, structPos));
        }
    };
    private MigolIOFunction fileSeekFunc = new MigolIOFunction() {

        public void executeIO(MigolExecutionSession session, int structPos) {
            io.submit(new FileSeekRequest(session, structPos));
        }
    };
    private MigolIOFunction fileSizeFunc = new MigolIOFunction() {

        public void executeIO(MigolExecutionSession session, int structPos) {
            io.submit(new FileSizeRequest(session, structPos));
        }
    };

    public MigolIOFunction getOpenFileFunc() {
        return openFileFunc;
    }

    public MigolIOFunction getFileSeekFunc() {
        return fileSeekFunc;
    }

    public MigolIOFunction getFileTellFunc() {
        return fileTellFunc;
    }

    public MigolIOFunction getFileSizeFunc() {
        return fileSizeFunc;
    }
    
    
}
