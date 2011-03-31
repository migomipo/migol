
package se.psilon.migomipo.migol2;

import se.psilon.migomipo.migol2.execute.*;
import se.psilon.migomipo.migol2.parse.MigolParsingException;

public interface MigolValue {
    public int fetchValue(MigolExecutionSession session) throws MigolExecutionException;
    public MigolValue postProcess(java.util.Map<String, Integer> constants) throws MigolParsingException;
}
