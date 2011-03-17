/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.psilon.migomipo.migol2.test;

import se.psilon.migomipo.migol2.execute.MigolExecutionSession;
import se.psilon.migomipo.migol2.parse.MigolParser;

/**
 *
 * @author John
 */
public class MigolTest {
    public static void main(String[] args) throws Throwable{
        MigolParser.parseString(
                "5<3,[5]>-, [#]>-, '7>-"
                ).executeProgram(new MigolExecutionSession());

    }
}
