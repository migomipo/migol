/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.psilon.migomipo.migol2.io;

import java.nio.ByteBuffer;
import java.io.*;
/**
 *
 * @author John
 */
public class StreamByteChannel implements java.nio.channels.ByteChannel {
    InputStream in;
    OutputStream out;
    boolean isOpen = true;

    public StreamByteChannel(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    public int read(ByteBuffer dst) throws IOException {
         if(in == null){
             return -1;
         }
         int space = dst.remaining();
         byte[] b = new byte[space];
         int bytesread = in.read(b);
         dst.put(b);
         return bytesread;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void close() throws IOException {
        isOpen = false;
        in.close();
        out.close();
    }

    public int write(ByteBuffer src) throws IOException {
         if(out == null){
             return 0;
         }
         int space = src.remaining();
         byte[] b = new byte[space];
         src.get(b);
         out.write(b);
         return space;
        
    }

}
