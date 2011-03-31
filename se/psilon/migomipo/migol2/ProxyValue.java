package se.psilon.migomipo.migol2;

import java.util.Map;
import se.psilon.migomipo.migol2.execute.MigolExecutionException;
import se.psilon.migomipo.migol2.execute.MigolExecutionSession;
import se.psilon.migomipo.migol2.parse.MigolParsingException;

public class ProxyValue implements MigolValue {

    private MigolValue val;

    public ProxyValue(MigolValue val) {
        this.val = val;
    }

    public int fetchValue(MigolExecutionSession session) throws MigolExecutionException {
        return val.fetchValue(session);
    }

    public MigolValue getVal() {
        return val;
    }

    public void setVal(MigolValue val) {
        this.val = val;
    }

    public MigolValue postProcess(Map<String, Integer> constants) throws MigolParsingException {
        return new ProxyValue(val.postProcess(constants));
    }




}
