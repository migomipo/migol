package se.psilon.migomipo.migol2.execute;
public class ConsoleOutputRegister implements MigolSpecialRegister {
    final int mode;
    public ConsoleOutputRegister(int mode){
        this.mode = mode;
    }

    public int read(MigolExecutionSession session) throws MigolExecutionException {
        return 0;
    }

    public void write(MigolExecutionSession session, int val) throws MigolExecutionException {
        if(mode == 1){
            System.out.print(val);
        } else if(mode == 2){
            System.out.print((char) val);
        }
    }

}
