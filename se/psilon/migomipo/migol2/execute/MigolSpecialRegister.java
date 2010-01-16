/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.psilon.migomipo.migol2.execute;

/**
 *
 * @author MigoMipo Software Design
 */
public interface MigolSpecialRegister {

    public int read() throws MigolExecutionException;
    public void write(int val) throws MigolExecutionException;

}
