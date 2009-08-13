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
 * Represents a conditional statement.
 *
 * All statements can be modified into a conditional statement in Migol by
 * appending a conditional suffix to it. When the statement is executed, the
 * conditional operation will be evaluated first. If the operation evaluates to
 * true, the statement will be executed. Otherwise, the statement will be
 * ignored and the program pointer progresses to the next statement.
 * @author John Eriksson
 */
public class ConditionalStatement implements MigolStatement {
    private static final long serialVersionUID = -4829698528440668056L;
    private ConditionalOperation cond;
    private MigolStatement statement;
    /**
     * Constructs a new conditional statement.
     * @param statement     The statement to be executed if the
     * conditional operation evaluates to true.
     * @param cond          The conditional operation.
     */
    public ConditionalStatement(MigolStatement statement, ConditionalOperation cond ) {       
        this.cond = cond;
        this.statement = statement;
    }

    public void executeStatement(MigolExecutionSession session) throws MigolExecutionException {
        boolean condres = cond.evaluate(session);
        if(condres){
            statement.executeStatement(session);
        } else {
            session.progressPP();
        }
    }

    public String toMigolSyntax() {
        return statement.toMigolSyntax() + cond.toMigolSyntax();
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
        final ConditionalStatement other = (ConditionalStatement) obj;
        if (this.cond != other.cond && (this.cond == null || !this.cond.equals(other.cond))) {
            return false;
        }
        if (this.statement != other.statement && (this.statement == null || !this.statement.equals(other.statement))) {
            return false;
        }
        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.cond != null ? this.cond.hashCode() : 0);
        hash = 71 * hash + (this.statement != null ? this.statement.hashCode() : 0);
        return hash;
    }


    
    

}
