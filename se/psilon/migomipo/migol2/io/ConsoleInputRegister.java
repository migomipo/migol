
package se.psilon.migomipo.migol2.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import se.psilon.migomipo.migol2.execute.MigolExecutionException;
import se.psilon.migomipo.migol2.execute.MigolExecutionSession;
import se.psilon.migomipo.migol2.execute.MigolSpecialRegister;

public class ConsoleInputRegister implements MigolSpecialRegister {
    Reader in;
    public ConsoleInputRegister(){
        this.in = new InputStreamReader(System.in);
    }
    public int read(MigolExecutionSession session) throws MigolExecutionException {
        try {
            return in.read();
        } catch(IOException ex){
            throw new MigolExecutionException(session.getPP());
        }
    }

    public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
    }

}
