/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.psilon.migomipo.migol2.parse;

import java.util.Map;
import se.psilon.migomipo.migol2.IntegerValue;
import se.psilon.migomipo.migol2.MigolValue;
import se.psilon.migomipo.migol2.execute.MigolExecutionException;
import se.psilon.migomipo.migol2.execute.MigolExecutionSession;

/**
 *
 * @author joheri01
 */
class ConstantValue implements MigolValue {
    private String name;
    private int defers;
    private String cLine;
    private int pos;
    private final int linenum;

    public ConstantValue(String name, int defers, String cLine, int linenum, int pos) {
        this.name = name;
        this.defers = defers;
        this.cLine = cLine;
        this.pos = pos;
        this.linenum = linenum;
    }

    public int fetchValue(MigolExecutionSession session) throws MigolExecutionException {
        throw new MigolExecutionException("Illegal ConstantValue object in parsed program", session.getPP());
    }

    public MigolValue postProcess(Map<String, Integer> constants) throws MigolParsingException {
        try {
            Integer i = constants.get(name);
            return new IntegerValue(i.intValue(), defers);
        } catch(NullPointerException ex){
            throw new MigolParsingException("Undefined constant referenced " +
                    "in line " + linenum, cLine, linenum, pos);
        }
    }



}
