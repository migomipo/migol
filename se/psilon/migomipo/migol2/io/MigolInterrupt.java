
package se.psilon.migomipo.migol2.io;

import se.psilon.migomipo.migol2.execute.MigolExecutionSession;

public interface MigolInterrupt {
    void enter(MigolExecutionSession session);
    
    void exit(MigolExecutionSession session);
    

}
