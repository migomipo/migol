/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.psilon.migomipo.migol2.test;

import java.net.*;
import java.io.*;
import java.nio.channels.*;
import java.nio.*;

/**
 *
 * @author John
 */
public class SocketTest {

    public static void main(String[] args) throws Throwable {
        (new Runnable() {

            public void run() {
                try {
                    ServerSocket s = new ServerSocket(30000);
                    Socket sock = s.accept();
                    BufferedOutputStream o = new BufferedOutputStream(sock.getOutputStream());                    
                    o.write('J');
                    o.write('O');
                    o.write('H');
                    o.write('N');
                    o.write('!');
                    o.flush();
                        
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).run();

        
                

    }
}
