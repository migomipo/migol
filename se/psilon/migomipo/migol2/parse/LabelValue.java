package se.psilon.migomipo.migol2.parse;

import java.util.Map;
import se.psilon.migomipo.migol2.IntegerValue;
import se.psilon.migomipo.migol2.ReadValue;
import se.psilon.migomipo.migol2.MigolExecutionException;
import se.psilon.migomipo.migol2.MigolExecutionSession;

class LabelValue implements ReadValue{
    private String label;
    private IntegerValue value = null;

    LabelValue(String label) {
        this.label = label;
    }

    void resolve(Map<String, Integer> vars){
        value = IntegerValue.getInstance(vars.get(label).intValue());
    }

    public int get(MigolExecutionSession session) throws MigolExecutionException {
        return value.get(session);
    }

    public int defer(MigolExecutionSession session) throws MigolExecutionException {
        return value.defer(session);
    }

    public void set(MigolExecutionSession session, int val) throws MigolExecutionException {
        value.set(session, val);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LabelValue other = (LabelValue) obj;
        if ((this.label == null) ? (other.label != null) : !this.label.equals(other.label)) {
            return false;
        }
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 29 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }



}
