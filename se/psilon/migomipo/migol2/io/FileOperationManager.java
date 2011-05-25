package se.psilon.migomipo.migol2.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import se.psilon.migomipo.migol2.execute.*;

public class FileOperationManager {

    private IOManager io;

    public FileOperationManager(IOManager io) {
        this.io = io;
    }

    private class FileOpenRequest implements Runnable {

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
            int filenamelen = mem[structPos + 1];
            int filenamepos = mem[structPos];
            byte[] bytes = new byte[filenamelen];

            for (int i = 0; i < filenamelen; i++) {
                bytes[i] = (byte) mem[filenamepos + i];
            }
            try {
                int filemode = mem[structPos + 2];
                String mode;
                if (filemode == 0) {
                    mode = "r";
                } else if (filemode == 1) {
                    mode = "rw";
                } else {
                    throw new IllegalArgumentException();
                }
                FileChannel f = new RandomAccessFile(new String(bytes, "utf-8"), mode).getChannel();
                handle = io.addChannel(f);

            } catch (IOException ex) {
                error = 1;
            } catch (IllegalArgumentException ex) {
                error = 2;
            }
            mem[structPos + 4] = error;
            mem[structPos + 5] = handle;
            session.getResultQueue().add(structPos);
        }
    }

    private MigolIOFunction openFileFunc = new MigolIOFunction() {

        public void executeIO(MigolExecutionSession session, int structPos) {
            io.submit(new FileOpenRequest(session, structPos));
        }
    };

    public MigolIOFunction getOpenFileFunc() {
        return openFileFunc;
    }


}
