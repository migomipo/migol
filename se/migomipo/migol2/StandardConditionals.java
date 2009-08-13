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

import se.migomipo.migol2.execute.MigolExecutionException;
import se.migomipo.migol2.execute.MigolExecutionSession;

/**
 * The set of the standard conditional operators in Migol.
 *
 * The standard set implements several common integer comparison operators.
 * All of the operators
 * {@link MigolValue#fetchValue(se.migomipo.migol2.execute.MigolExecutionSession) fetches}
 * the value of a {@link MigolValue} object and compares it with 0.
 * The available operators are <code>?=<code> (equal), "?&lt;&gt;" (not equal),
 * "?&lt;" (less than), "?&gt;" (greater than), "?&lt;=" (less than or equal),
 * and "?&gt;>=" (greater than or equal).
 *
 * @author John Eriksson
 */
public class StandardConditionals implements ConditionalOperation {
    private static final long serialVersionUID = -2665767961367919383L;
    /**
     * Constant representing "greater than 0"-comparison operator.
     */
    public static final int COND_GT = 1;
    // ?>
    /**
     * Constant representing "less than 0"-comparison operator.
     */
    public static final int COND_LT = 2;
    // ?<
    /**
     * Constant representing "greater than or equal to 0"-comparison
     * operator.
     */
    public static final int COND_GTEQ = 3;
    // ?>=
    /**
     * Constant representing "less than or equal to 0"-comparison operator.
     */
    public static final int COND_LTEQ = 4;
    // ?<=
    /**
     * Constant representing "equal to 0"-comparison operator.
     */
    public static final int COND_EQ = 5;
    // ?=
    /**
     * Constant representing "not equal to 0"-comparison operator.
     */
    public static final int COND_NEQ = 6;
    // ?<>
    /**
     * The value which will be compared with the operator.
     */
    private final MigolValue value;
    /**
     * The operator used in this instance.
     */
    private final int op;

    public StandardConditionals(int op, MigolValue val) {
        super();
        this.op = op;
        this.value = val;
    }

    /**
     * Performs the comparison and returns the result.
     *
     * @param session
     * @return  The result of the comparison as a boolean.
     * @throws se.migomipo.migol2.execute.MigolExecutionException
     */
    public boolean evaluate(MigolExecutionSession session) throws MigolExecutionException {
        int val = value.fetchValue(session);
        switch (op) {
            case COND_GT:
                return val > 0;
            case COND_LT:
                return val < 0;
            case COND_GTEQ:
                return val >= 0;
            case COND_LTEQ:
                return val <= 0;
            case COND_EQ:
                return val == 0;
            case COND_NEQ:
                return val != 0;
            default:
                throw new MigolExecutionException("Unknown conditional " + "operator at statement " + session.getPP());
        }
    }
    /**
     * {@inheritDoc}
     */
    public String toMigolSyntax() {
        switch (op) {
            case COND_EQ:
                return "?=" + value.toMigolSyntax();
            case COND_NEQ:
                return "?<>" + value.toMigolSyntax();
            case COND_LTEQ:
                return "?<=" + value.toMigolSyntax();
            case COND_GTEQ:
                return "?>=" + value.toMigolSyntax();
            case COND_LT:
                return "?<" + value.toMigolSyntax();
            case COND_GT:
                return "?>" + value.toMigolSyntax();
            default:
                return "";
        }
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
        final StandardConditionals other = (StandardConditionals) obj;
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        if (this.op != other.op) {
            return false;
        }
        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 79 * hash + this.op;
        return hash;
    }

}
