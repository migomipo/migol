package se.migomipo.migol;

import java.util.Map;


public class LabelValue implements MigolValue{
    private String label;
    private IntegerValue value = null;

    public LabelValue(String label) {
        this.label = label;
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

    public String getLabel() {
        return label;
    }

    public IntegerValue getValue() {
        return value;
    }

    public void setValue(IntegerValue value) {
        this.value = value;
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
        int hash = 5;
        hash = 29 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 29 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
    
    

}
