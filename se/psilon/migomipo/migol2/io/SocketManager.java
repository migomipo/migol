package se.psilon.migomipo.migol2.io;

import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;
import se.psilon.migomipo.migol2.execute.MigolExecutionException;
import se.psilon.migomipo.migol2.execute.MigolExecutionSession;
import se.psilon.migomipo.migol2.execute.MigolSpecialRegister;

public class SocketManager {

    private IOManager io;
    private int ipAddressAddress;
    private int ipAddressMode = 0;
    private int port = 0;
    private int openSocketInterruptAddress = 0;
    private MigolSpecialRegister openSocketRegister = new OpenSocketRegister();
    private MigolSpecialRegister ipAddressAddressRegister = new IPAddressAddressRegister();
    private MigolSpecialRegister ipAddressModeRegister = new IPAddressModeRegister();
    private MigolSpecialRegister portRegister = new IPPortRegister();
    private MigolSpecialRegister intIpAddrAddressRegister = new InterruptIPAddressAddressRegister();
    private MigolSpecialRegister intIpPortRegister = new InterruptPortRegister();
    private MigolSpecialRegister intHandleRegister = new InterruptSocketHandleRegister();
    private MigolSpecialRegister intErrnoRegister = new InterruptErrorRegister();

    public SocketManager(IOManager io) {
        this.io = io;
    }

    public int getOpenSocketInterruptAddress() {
        return openSocketInterruptAddress;
    }

    private class OpenSocketInterrupt implements MigolInterrupt {

        private final int ipAddressAddress;
        private final int handle;
        private final int errno;
        private final int port;

        public OpenSocketInterrupt(int ipAddressAddress, int port, int handle, int errno) {
            this.ipAddressAddress = ipAddressAddress;
            this.handle = handle;
            this.errno = errno;
            this.port = port;
        }

        public int getHandle() {
            return handle;
        }

        public int getIpAddressAddress() {
            return ipAddressAddress;
        }

        public int getErrno() {
            return errno;
        }

        public int getPort() {
            return port;
        }

        public int getHandlerAddress(MigolExecutionSession session) {
            return SocketManager.this.getOpenSocketInterruptAddress();
        }
    }

    private class OpenSocketRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            if (ipAddressMode != 0 && ipAddressMode != 1) {
                return -1;
            }
            io.submit(new OpenSocketRequest(ipAddressAddress, ipAddressMode, port, session));
            return 1;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    private class IPAddressAddressRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return ipAddressAddress;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            ipAddressAddress = val;
        }
    }

    private class IPAddressModeRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return ipAddressMode;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            ipAddressMode = val;
        }
    }

    private class IPPortRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return port;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            port = val;
        }
    }

    private class OpenSocketInterruptHandlerRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            return openSocketInterruptAddress;
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
            openSocketInterruptAddress = val;
        }
    }

    private class InterruptIPAddressAddressRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            MigolInterrupt curInterrupt = session.getCurInterrupt();
            if (curInterrupt != null && curInterrupt instanceof OpenSocketInterrupt) {
                return ((OpenSocketInterrupt) curInterrupt).getIpAddressAddress();
            } else {
                return 0;
            }
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    private class InterruptSocketHandleRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            MigolInterrupt curInterrupt = session.getCurInterrupt();
            if (curInterrupt != null && curInterrupt instanceof OpenSocketInterrupt) {
                return ((OpenSocketInterrupt) curInterrupt).getHandle();
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
            if (curInterrupt != null && curInterrupt instanceof OpenSocketInterrupt) {
                return ((OpenSocketInterrupt) curInterrupt).getErrno();
            } else {
                return 0;
            }
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    private class InterruptPortRegister implements MigolSpecialRegister {

        public int read(MigolExecutionSession session) throws MigolExecutionException {
            MigolInterrupt curInterrupt = session.getCurInterrupt();
            if (curInterrupt != null && curInterrupt instanceof OpenSocketInterrupt) {
                return ((OpenSocketInterrupt) curInterrupt).getPort();
            } else {
                return 0;
            }
        }

        public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        }
    }

    public MigolSpecialRegister getPortRegister() {
        return portRegister;
    }

    public MigolSpecialRegister getIpAddressAddressRegister() {
        return ipAddressAddressRegister;
    }

    public MigolSpecialRegister getIpAddressModeRegister() {
        return ipAddressModeRegister;
    }

    public MigolSpecialRegister getOpenSocketRegister() {
        return openSocketRegister;
    }

    public MigolSpecialRegister getOpenSocketInterruptHandlerRegister() {
        return new OpenSocketInterruptHandlerRegister();
    }

    public MigolSpecialRegister getIntErrnoRegister() {
        return intErrnoRegister;
    }

    public MigolSpecialRegister getIntHandleRegister() {
        return intHandleRegister;
    }

    public MigolSpecialRegister getIntIpAddrAddressRegister() {
        return intIpAddrAddressRegister;
    }

    public MigolSpecialRegister getIntIpPortRegister() {
        return intIpPortRegister;
    }

    private class OpenSocketRequest implements Runnable {

        private final int port;
        private final int ipAddressAddress;
        private final int ipAddressMode;
        private final MigolExecutionSession session;

        private OpenSocketRequest(int ipAddressAddress, int ipAddressMode, int port, MigolExecutionSession session) {
            this.ipAddressAddress = ipAddressAddress;
            this.ipAddressMode = ipAddressMode;
            this.port = port;
            this.session = session;
        }

        public void run() {
            try {
                byte[] ip;
                int[] memory = session.getMemory();
                if (ipAddressMode == 0) {
                    ip = new byte[4];
                    for (int i = 0; i < 4; i++) {
                        ip[i] = (byte) memory[ipAddressAddress + i];
                    }
                } else if (ipAddressMode == 1) {
                    ip = new byte[16];
                    for (int i = 0; i < 16; i++) {
                        ip[i] = (byte) memory[ipAddressAddress + i];
                    }
                } else {
                    throw new IllegalStateException();
                }
                SocketChannel ch = SocketChannel.open(new InetSocketAddress(InetAddress.getByAddress(ip), port));
                int pos = io.addChannel(ch);
                session.getInterruptQueue().add(new OpenSocketInterrupt(ipAddressAddress, port, pos, 0));
            } catch (Exception ex) {
                session.getInterruptQueue().add(new OpenSocketInterrupt(ipAddressAddress, port, -1, 1));
            }
        }
    }
}
