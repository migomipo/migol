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

import se.psilon.migomipo.migol2.execute.*;


/**
 * A NOP Operation statement, which does nothing except moving the program
 * pointer to the next statement.
 *
 * The NOP statement in Migol is <code>_</code>.
 * @author John Eriksson
 */
public class NopStatement implements MigolStatement {
    private static final long serialVersionUID = 8187417599523176457L;
    /**
     * Singleton instance for a NopStatement. Since NopStatement objects contain
     * no data, it is recommended to use this instance instead of creating
     * NopStatement objects.
     */
    public static final NopStatement __INSTANCE__;
    static {
        __INSTANCE__ = new NopStatement();
    }
    /**
     * Does nothing, besides increasing the program pointer by 1.
     * @param session   The session object to which the statement will be
     * performed.
     *
     */
    public void executeStatement(MigolExecutionSession session){
    }
    /**
     * {@inheritDoc MigolStatement}
     */
    public String toMigolSyntax() {
       return "_";
    }

    public MigolValue getTarget() {
        return null;
    }

    public AssignmentOperation[] getOperations() {
        return new AssignmentOperation[0];
    }


}
