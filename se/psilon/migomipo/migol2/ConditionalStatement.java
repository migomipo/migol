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
package se.psilon.migomipo.migol2;

import se.psilon.migomipo.migol2.execute.MigolExecutionException;
import se.psilon.migomipo.migol2.execute.MigolExecutionSession;

/**
 * Represents a conditional statement.
 *
 * All statements can be modified into a conditional statement in Migol by
 * appending a conditional suffix to it. When the statement is executed, the
 * conditional operation will be evaluated first. If the operation evaluates to
 * true, the statement will be executed. Otherwise, the statement will be
 * ignored and the program pointer progresses to the next statement.
 *
 * A ConditionalStatement object wraps an another {@link MigolStatement} object.
 * It evaluates a {@link ConditionalOperation} object, and executes the wrapped
 * {@link MigolStatement} object if the result is {@code true}.
 * @author John Eriksson
 * @see MigolStatement
 * @see ConditionalOperation
 */
public class ConditionalStatement implements MigolStatement {
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

    private static final long serialVersionUID = -4829698528440668056L;
    private final MigolStatement statement;
    private final int condtype;
    private final MigolValue value;
    /**
     * Constructs a new conditional statement.
     * @param statement     The statement to be executed if the
     * conditional operation evaluates to true.
     * @param cond          The conditional operation.
     */
    public ConditionalStatement(MigolStatement statement, int condtype, MigolValue value) {
        this.condtype = condtype;
        this.value = value;
        this.statement = statement;
    }

    /**
     * Executes this conditional statement. It first evaluates it's
     * {@link ConditionalOperation} object.
     * If it evaluates to true, the wrapped statement will be executed, if false,
     * it will progress the program pointer to the next statement
     * @param session       The session object to be manipulated
     * @throws se.psilon.migomipo.migol2.execute.MigolExecutionException   If an error
     * occurs during execution.
     */
    public void executeStatement(MigolExecutionSession session) throws MigolExecutionException {
        boolean condres = evaluate(session);
        if(condres){
            statement.executeStatement(session);
        } 
    }

    private boolean evaluate(MigolExecutionSession session) throws MigolExecutionException {
        int val = value.fetchValue(session);
        switch (condtype) {
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
                throw new MigolExecutionException("Unknown conditional " +
                        "operator at statement " + session.getPP(),session.getPP());
        }
    }
    /**
     * {@inheritDoc MigolStatement}
     */
    public String toMigolSyntax() {
        throw new UnsupportedOperationException();
    }

    public MigolValue getTarget() {
        return statement.getTarget();
    }

    public AssignmentOperation[] getOperations() {
        return statement.getOperations();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConditionalStatement other = (ConditionalStatement) obj;
        if (this.statement != other.statement && (this.statement == null || !this.statement.equals(other.statement))) {
            return false;
        }
        if (this.condtype != other.condtype) {
            return false;
        }
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.statement != null ? this.statement.hashCode() : 0);
        hash = 43 * hash + this.condtype;
        hash = 43 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
    
    


}
