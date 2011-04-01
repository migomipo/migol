package se.psilon.migomipo.migol2.io;

import se.psilon.migomipo.migol2.execute.MigolExecutionSession;

class IOInterrupt implements MigolInterrupt {

    private final int bufferAddress;
    private final int bytes;
    private final int socketHandle;
    private final int type;
    private final IOManager outer;

    public IOInterrupt(IOManager outer, int bufferAddress, int bytes, int socketHandle, int type) {
        super();
        this.outer = outer;
        this.bufferAddress = bufferAddress;
        this.bytes = bytes;
        this.socketHandle = socketHandle;
        this.type = type;
    }

    public void enter(MigolExecutionSession session) {
        outer.setCurInterrupt(this);
        session.doInterrupt(outer.getHandlerAddress());
    }

    public void exit(MigolExecutionSession session) {
        outer.setCurInterrupt(null);
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
    
    
}
