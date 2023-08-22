import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.*;


public class ServerMain {

    public static void main(String[] args) throws IOException {
        Server.loadProperties();
        Server.loadData();
        Server.publishObject();
        Server.initMulticastSocket();
        ServerSocket connection = Server.openSocket();
        Runtime.getRuntime().addShutdownHook(new ShutdownHandler()); //per gestire l'interruzione del server
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(); //executor per l'aggiornamento della parola ogni tot secondi
        executor.scheduleAtFixedRate(new WordUpdaterTask(), 0, Server.getDelay(), TimeUnit.SECONDS);
        ExecutorService pool = Executors.newCachedThreadPool(); //pool per i task che gestiscono la connessione con il client
        try{
            while(true){
                pool.execute(new ServerTask(connection.accept()));
            }
        }catch(IOException e){}
    }
}
