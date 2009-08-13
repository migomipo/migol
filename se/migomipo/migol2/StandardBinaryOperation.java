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

public class StandardBinaryOperation implements AssignmentOperation {
    private static final long serialVersionUID = -5844275926070860555L;
    /**
     * Constant representing an absolute assignment operation.
     */
    public static final int OP_ASSIGN = 1;
    // <
    /**
     * Constant representing an addition operation.
     */
    public static final int OP_PLUS = 2;
    // <$+
    /**
     * Constant representing a subtraction operation.
     */
    public static final int OP_MINUS = 3;
    // <$-
    /**
     * Constant representing a multiplication operation.
     */
    public static final int OP_TIMES = 4;
    // <$*
    /**
     * Constant representing a division operation.
     */
    public static final int OP_DIVIDE = 5;
    // <$/
    /**
     * Constant representing a modulo operation.
     */
    public static final int OP_MOD = 6;
    // <$%
    /**
     * Constant representing a bitwise AND operation.
     */
    public static final int OP_AND = 7;
    // <$&
    /**
     * Constant representing a bitwise OR operation.
     */
    public static final int OP_OR = 8;
    // <$|
    /**
     * Constant representing a bitwise XOR operation.
     */
    public static final int OP_XOR = 9;
    // <$^
    /**
     * Constant representing a left bitshift operation.
     */
    public static final int OP_LSH = 10;
    // <$<<
    /**
     * Constant representing a right arithmetic bitshift operation.
     */
    public static final int OP_RSHA = 11;
    // <$>>
    /**
     * Constant representing a right logical bitshift operation.
     */
    public static final int OP_RSHL = 12;
    // <$>>>
    /**
     * Constant representing a left bit rotation operation.
     */
    public static final int OP_LRO = 13;
    // <$<<_
    /**
     * Constant representing a right rotation operation.
     */
    public static final int OP_RRO = 14;
    // <$>>_
    /**
     * The selected operation for this object.
     */
    private final int operation;
    /**
     * The second value for the operation.
     */
    private MigolValue value;

    public StandardBinaryOperation(int operation, MigolValue val) {
        super();
        this.operation = operation;
        this.value = val;
    }

    /**
     * Performs this operation.
     * @param session   The session object to which the statement will be performed.
     * @param currentvalue  The current value of the object to be modified.
     * @return  The result of this operation.
     * @throws se.migomipo.migol2.execute.MigolExecutionException
     * If the operation constant is unknown.
     */
    public int operation(MigolExecutionSession session, int currentvalue) throws MigolExecutionException {
        int cal = value.fetchValue(session);
        switch (operation) {
            case OP_ASSIGN:
                return cal;
            case OP_PLUS:
                return cal + currentvalue;
            case OP_MINUS:
                return currentvalue - cal;
            case OP_TIMES:
                return currentvalue * cal;
            case OP_DIVIDE:
                return currentvalue / cal;
            case OP_MOD:
                return currentvalue % cal;
            case OP_AND:
                return currentvalue & cal;
            case OP_OR:
                return currentvalue | cal;
            case OP_XOR:
                return currentvalue ^ cal;
            case OP_LSH:
                return currentvalue << cal;
            case OP_RSHA:
                return currentvalue >> cal;
            case OP_RSHL:
                return currentvalue >>> cal;
            case OP_LRO:
                return rotl(currentvalue, cal);
            case OP_RRO:
                return rotr(currentvalue, cal);
            default:
                throw new MigolExecutionException("Unknown operator at statement " + session.getPP());
        }
    }

    private int rotl(int value, int shift) {
        shift &= 31;
        return (value << shift) | (value >>> (32 - shift));
    }

    private int rotr(int value, int shift) {
        shift &= 31;
        return (value >>> shift) | (value << (32 - shift));
    }
    /**
     * {@inheritDoc}
     */
    public String toMigolSyntax() {
        switch (operation) {
            case OP_ASSIGN:
                return "<" + value.toMigolSyntax();
            case OP_PLUS:
                return "<$+" + value.toMigolSyntax();
            case OP_MINUS:
                return "<$-" + value.toMigolSyntax();
            case OP_TIMES:
                return "<$*" + value.toMigolSyntax();
            case OP_DIVIDE:
                return "<$/" + value.toMigolSyntax();
            case OP_MOD:
                return "<$%" + value.toMigolSyntax();
            case OP_AND:
                return "<$&" + value.toMigolSyntax();
            case OP_OR:
                return "<$|" + value.toMigolSyntax();
            case OP_XOR:
                return "<$^" + value.toMigolSyntax();
            case OP_LSH:
                return "<$<<" + value.toMigolSyntax();
            case OP_RSHA:
                return "<$>>" + value.toMigolSyntax();
            case OP_RSHL:
                return "<$>>>" + value.toMigolSyntax();
            case OP_LRO:
                return "<$<<_" + value.toMigolSyntax();
            case OP_RRO:
                return "<$>>_" + value.toMigolSyntax();
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
        final StandardBinaryOperation other = (StandardBinaryOperation) obj;
        if (this.operation != other.operation) {
            return false;
        }
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + this.operation;
        hash = 19 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }


}
