
package se.psilon.migomipo.migol2.io;

import se.psilon.migomipo.migol2.execute.MigolExecutionSession;

public interface MigolIOFunction {
    public void executeIO(MigolExecutionSession session, int structPos);
}
