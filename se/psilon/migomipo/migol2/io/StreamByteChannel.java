/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.psilon.migomipo.migol2.io;

import java.nio.ByteBuffer;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author John
 */
public class StreamByteChannel implements java.nio.channels.ByteChannel {

    private ReadableByteChannel in;
    private WritableByteChannel out;
   

    public StreamByteChannel(InputStream in, OutputStream out) {
        this.in = (in != null) ? Channels.newChannel(in) : null;
        this.out = (out != null) ? Channels.newChannel(out) : null;
    }

    public int read(ByteBuffer dst) throws IOException {
        if (in == null) {
            return 0;
        }
        return in.read(dst);
    }

    public boolean isOpen() {
        return in.isOpen() || out.isOpen();
    }

    public void close() throws IOException {
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
    }

    public int write(ByteBuffer src) throws IOException {
        if (out == null) {
            return 0;
        }
        return out.write(src);
    }
}
