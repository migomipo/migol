/*
 * Copyright (c) 2009 John Eriksson

 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:

 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package se.migomipo.migol2;

import se.migomipo.migol2.execute.MigolExecutionSession;
import se.migomipo.migol2.execute.MigolExecutionException;
import se.migomipo.migol2.execute.MigolIOExecutionException;

/**
 * An output statement, which prints a character or number to output.
 *
 * An output statement in Migol looks like <code>value&gt;</code> or
 * <code>value&gt;-</code>, where value is any valid Migol value, such as
 * <code>[#]</code> and <code>[[[45]]]</code>. The &gt; operator prints out
 * the value as a character, and the &gt;- prints out the value as a number.
 *
 * Output statements can be conditional, simply by appending a conditional
 * operator after. An example is <code>[4]&gt;?&lt;[3]</code>.
 *
 * @author John Eriksson
 */
public class OutputStatement implements MigolStatement {

    private static final long serialVersionUID = 3716239141055188988L;
    /**
     * The value to be printed.
     */
    private MigolValue value;
    /**
     * Specifies whether this object should print the value as a character
     * or as a number.
     */
    private final int mode;

    /**
     * Performs this statement, and prints the value to the screen.
     * @param session   The session that the value is fetched from.
     * @throws se.migomipo.migol2.execute.MigolExecutionException
     * If the selected mode is unknown.
     */
    public void executeStatement(MigolExecutionSession session) throws MigolExecutionException {
        try {
            int val = value.fetchValue(session);
            if (mode == 1) {
                session.getIOCallback().outputChar(val);
            } else if (mode == 2) {
                session.getIOCallback().outputInt(val);
            } else {
                throw new MigolExecutionException("Unknown output mode at statement " + session.getPP());
            }
            session.progressPP();
        } catch (java.io.IOException e) {
            throw new MigolIOExecutionException("I/O operation failed at statement " + session.getPP(), e);
        }
    }

    /**
     * Creates a new output statement object.
     * 
     * @param value The value to be printed
     * @param mode  The printing mode.
     */
    public OutputStatement(MigolValue value, int mode) {
        if (mode != 1 && mode != 2) {
            throw new IllegalArgumentException();
        }
        this.value = value;
        this.mode = mode;
    }

    public String toMigolSyntax() {
        String print = null;
        switch (mode) {
            case 1:
                print = ">";
                break;
            case 2:
                print = ">-";
                break;
            default:
                return "";
        }
        return value.toMigolSyntax() + print;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 23 * hash + this.mode;
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OutputStatement other = (OutputStatement) obj;
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        if (this.mode != other.mode) {
            return false;
        }
        return true;
    }
}
