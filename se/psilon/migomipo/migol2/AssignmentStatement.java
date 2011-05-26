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

import java.util.Arrays;

/**
 * Represents a Migol assignment statement.
 *
 * Assignment statements are the central part of the Migol programming language.
 * All mathemathical operations and even branching are made with assignment
 * statements.
 *
 * An assignment statement consists of:
 * <ul>
 * <li>A target value. This is the address which will be manipulated. </li>
 * <li>One or more assignment operations. Each assignment operation is a
 * operator and optionally a value. They will each be sequentially applied to the target
 * value.</li>
 * <li>Like output statements, an assignment statement may contain a conditional
 * operator, which makes the operation conditional.
 * If the conditional operation evaluates to false, the statement won't be
 * executed.</li>
 * </ul>
 * @author John Eriksson
 */
public class AssignmentStatement implements MigolStatement {

    private static final long serialVersionUID = -7856706192040130177L;
    /**
     * The target address.
     */
    private WriteValue target;
    /**
     * The assignment operators.
     */
    private AssignmentOperation[] operations;


    /**
     * Creates a new object, representing a Migol assignment statement.
     * @param target    The target address.
     * @param operation The set of operations to be performed on the target address.
     */
    public AssignmentStatement(WriteValue target, AssignmentOperation[] operation) {
        this.target = target;
        this.operations = operation;
    }
    /**
     * Executes the assignment statement.
     *
     * The operations are performed on the session object supplied as an argument.
     * 
     * The first step is to evaluate the conditional. If the result is false,
     * the method updates the program pointer and returns. Otherwise, it
     * evaluates the target value to get the address which will be modified,
     * and sequentially applies the assignment operations to this address.
     * The target address may be an integer address or the program pointer.
     * All updates are written to the target value immediately, and can not
     * be cached.
     *
     * 0&lt;3, 0&lt;[0]&lt;$*2&lt;[0]&lt;$*2, [0]&gt;- prints out 12 and not 6,
     * since each assignment writes directly to memory cell 0, and the [0] returns
     * the current value after each assignment. In all respects, compound
     * assignment statements are semantically equivalent to separated statements,
     * except that they count as a single statement by the program.
     * If the conditional operation evaluates to true, ALL operations must be
     * executed.
     *
     * 
     * @param session   The session object to which the statement will be performed.
     * @throws se.psilon.migomipo.migol2.execute.MigolExecutionException If an error
     * occurs during the operation.
     */
    public void executeStatement(MigolExecutionSession session) throws MigolExecutionException {              
        for(AssignmentOperation op : operations){           
            op.operation(session, target);
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AssignmentStatement other = (AssignmentStatement) obj;
        if (this.target != other.target && (this.target == null || !this.target.equals(other.target))) {
            return false;
        }
        if (this.operations != other.operations && (this.operations == null || !Arrays.equals(this.operations, other.operations))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.target != null ? this.target.hashCode() : 0);
        hash = 37 * hash + (this.operations != null ? this.operations.hashCode() : 0);
        return hash;
    }
}
