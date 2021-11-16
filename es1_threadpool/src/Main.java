import java.util.*;
import java.util.concurrent.RejectedExecutionException;

public class Main {
    public static void main(String args[]) throws Exception{
        Server server = new Server();
        for(int i = 0; i < 50; i++){
            Task task =  new Task(i);

            try {
                server.executeTask(task);
            } catch(RejectedExecutionException e){
                System.out.println("Viaggiatore no. " + i + ":sala esaurita");
            }

            Thread.sleep(50);
        }
        server.endServer();
    }
}
