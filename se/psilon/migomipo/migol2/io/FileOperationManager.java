package se.psilon.migomipo.migol2.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import se.psilon.migomipo.migol2.execute.MigolExecutionException;
import se.psilon.migomipo.migol2.execute.MigolExecutionSession;
import se.psilon.migomipo.migol2.execute.MigolSpecialRegister;

public class FileOperationManager {

    private IOManager io;
    private int namepos = 0, namelen = 0;
    private int filemode = 0;
    private int fileopenhandler = 0;
    private MigolSpecialRegister fileSeekRegister = new FileSeekRegister();
    private MigolSpecialRegister fileSizeRegister = new FileSizeRegister();
    private MigolSpecialRegister fileOpenRegister = new FileOpenRegister();
    private MigolSpecialRegister fileNameLengthRegister = new FileNameLengthRegister();
    private MigolSpecialRegister fileNameAddressRegister = new FileNameAddressRegister();
    private MigolSpecialRegister fileModeRegister = new FileModeRegister();
    private MigolSpecialRegister fileOpenInterruptNameLengthRegister =
            new FileOpenInterruptNameLenRegister();
    private MigolSpecialRegister fileOpenInterruptNameAddressRegister =
            new FileOpenInterruptNamePosRegister();
    private MigolSpecialRegister fileOpenInterruptHandleRegister =
            new FileOpenInterruptHandleRegister();
    private MigolSpecialRegister fileOpenInterruptErrorRegister =
            new FileOpenInterruptErrorRegister();
    
        

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

    private class FileOpenRegister implements MigolSpecialRegister {

        private class FileOpenRequest implements Runnable, MigolInterrupt {

            private MigolExecutionSession session;
            private int filenamepos, filenamelen, filemode;
            private int error;
            private int fd;

            public FileOpenRequest(MigolExecutionSession session, int filenamepos, int filenamelen, int filemode) {
                this.session = session;
                this.filenamepos = filenamepos;
                this.filenamelen = filenamelen;
                this.filemode = filemode;
                this.error = 0;
                this.fd = -1;
            }

            public void run() {
                ByteArrayOutputStream bos = new ByteArrayOutputStream(filenamelen);
                int[] memory = session.getMemory();
                for (int i = 0; i < filenamelen; i++) {
                    bos.write(memory[filenamepos + i]);
                }
                try {
                    String mode;
                    if (filemode == 0) {
                        mode = "r";
                    } else if (filemode == 1) {
                        mode = "rw";
                    } else {
                        throw new IllegalStateException();
                    }
                    FileChannel f = new RandomAccessFile(new String(bos.toByteArray(), "utf-8"), mode).getChannel();
                    fd = io.addChannel(f);

                } catch (Exception ex) {
                    fd = -1;
                    error = 1;
                }
                session.getInterruptQueue().add(this);
            }

            public int getHandlerAddress(MigolExecutionSession session) {
                return fileopenhandler;
            }

            public int getError() {
                return error;
            }

            public int getFd() {
                return fd;
            }

            public int getFilenamelen() {
                return filenamelen;
            }

            public int getFilenamepos() {
                return filenamepos;
            }
        }

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            io.submit(new FileOpenRequest(session,
                    FileOperationManager.this.namepos,
                    FileOperationManager.this.namelen,
                    FileOperationManager.this.filemode));
            return 0;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    private class FileNameAddressRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return namepos;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            namepos = val;
        }
    }

    private class FileNameLengthRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return namelen;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            namelen = val;
        }
    }

    private class FileModeRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return filemode;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            filemode = val;
        }
    }

    private class FileOpenInterruptNamePosRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            MigolInterrupt curInterrupt = session.getCurInterrupt();
            if (curInterrupt != null && curInterrupt instanceof FileOpenRegister.FileOpenRequest) {
                return ((FileOpenRegister.FileOpenRequest) curInterrupt).getFilenamepos();
            } else {
                return 0;

            }
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    private class FileOpenInterruptNameLenRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            MigolInterrupt curInterrupt = session.getCurInterrupt();
            if (curInterrupt != null && curInterrupt instanceof FileOpenRegister.FileOpenRequest) {
                return ((FileOpenRegister.FileOpenRequest) curInterrupt).getFilenamelen();
            } else {
                return 0;

            }
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }
    
    private class FileOpenInterruptHandleRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            MigolInterrupt curInterrupt = session.getCurInterrupt();
            if (curInterrupt != null && curInterrupt instanceof FileOpenRegister.FileOpenRequest) {
                return ((FileOpenRegister.FileOpenRequest) curInterrupt).getFd();
            } else {
                return 0;

            }
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }
    
    private class FileOpenInterruptErrorRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            MigolInterrupt curInterrupt = session.getCurInterrupt();
            if (curInterrupt != null && curInterrupt instanceof FileOpenRegister.FileOpenRequest) {
                return ((FileOpenRegister.FileOpenRequest) curInterrupt).getError();
            } else {
                return 0;
            }
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }    

    public MigolSpecialRegister getFileNameAddressRegister() {
        return fileNameAddressRegister;
    }

    public MigolSpecialRegister getFileNameLengthRegister() {
        return fileNameLengthRegister;
    }

    public MigolSpecialRegister getFileOpenRegister() {
        return fileOpenRegister;
    }

    public MigolSpecialRegister getFileSeekRegister() {
        return fileSeekRegister;
    }

    public MigolSpecialRegister getFileSizeRegister() {
        return fileSizeRegister;
    }

    public MigolSpecialRegister getFileModeRegister() {
        return fileModeRegister;
    }

    public MigolSpecialRegister getFileOpenInterruptErrorRegister() {
        return fileOpenInterruptErrorRegister;
    }

    public MigolSpecialRegister getFileOpenInterruptHandleRegister() {
        return fileOpenInterruptHandleRegister;
    }

    public MigolSpecialRegister getFileOpenInterruptNameAddressRegister() {
        return fileOpenInterruptNameAddressRegister;
    }

    public MigolSpecialRegister getFileOpenInterruptNameLengthRegister() {
        return fileOpenInterruptNameLengthRegister;
    }
    
    
}
