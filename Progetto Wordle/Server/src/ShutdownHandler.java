import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShutdownHandler extends Thread{
    @Override
    /**
     * Gestisce l'interruzione del server chiudendo le connessioni e salvando i dati
     */
    public void run(){
        try{
            Server.saveData();
            Server.closeConnections();
        } catch (IOException e) {
            Logger.getLogger(ShutdownHandler.class.getName()).log(Level.SEVERE, null, e);
        }
        System.out.println("Server fermato");
    }
}
