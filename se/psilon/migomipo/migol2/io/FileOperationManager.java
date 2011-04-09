package se.psilon.migomipo.migol2.io;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import se.psilon.migomipo.migol2.execute.MigolExecutionException;
import se.psilon.migomipo.migol2.execute.MigolExecutionSession;
import se.psilon.migomipo.migol2.execute.MigolSpecialRegister;

public class FileOperationManager {

    private IOManager io;

    public FileOperationManager(IOManager io) {
        this.io = io;

    }

    private class FileSeekRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            ByteChannel b = io.getCurrentChannel();
            if (b instanceof FileChannel) {
                FileChannel f = (FileChannel) b;
                try {
                    return ((int) f.position());
                } catch (IOException ex) {
                }
            }
            return -1;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            ByteChannel b = io.getCurrentChannel();
            if (b instanceof FileChannel) {
                FileChannel f = (FileChannel) b;
                try {
                    f.position(val);
                } catch (IOException ex) {
                }
            }
            
        }
    }
    
    private class FileSizeRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            ByteChannel b = io.getCurrentChannel();
            if (b instanceof FileChannel) {
                FileChannel f = (FileChannel) b;
                try {
                    return ((int) f.size());
                } catch (IOException ex) {
                }
            }
            return -1;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
                        
        }
    }
}
