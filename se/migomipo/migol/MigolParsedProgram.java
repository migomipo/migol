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
package se.migomipo.migol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A pre-parsed Migol program that can be executed on a {@link MigolExecutionSession}.
 *
 * <code>MigolParsedProgram</code> objects are returned from the Migol code parser.
 * It executes the program by looping through an array of {@link MigolStatement}
 * objects and executing them.
 *
 * Instances of this class are not safe for use by multiple concurrent threads.
 *
 * @see MigolExecutionSession
 * @see MigolStatement
 * @author John Eriksson
 */
public class MigolParsedProgram implements Serializable {

    /**
     * The rendered {@link MigolStatement} objects to be executed.
     */
    private final List<MigolStatement> statements;

    /**
     * Returns the number of statements in the program.
     * @return The number of statements in the program as an integer.
     */
    public int size() {
        return statements.size();
    }

    

    /**
     * Creates a new MigolParsedProgram object containing the statements
     * in the given argument.
     * @param statements    The statements in the new program.
     * @see MigolStatement
     */
    public MigolParsedProgram(MigolStatement[] statements) {
        this.statements = Arrays.asList(statements);
    }

    public MigolParsedProgram(List<MigolStatement> statements){
        this.statements = new ArrayList<MigolStatement>(statements);
    }

    public List<MigolStatement> getStatements(){
        return java.util.Collections.unmodifiableList(statements);
    }

    public MigolStatement getStatement(int pos){
        return statements.get(pos - 1);
    }

    public void executeProgram(MigolExecutionSession session) throws MigolExecutionException {
        session.executeProgram(this);
    }
        
}
