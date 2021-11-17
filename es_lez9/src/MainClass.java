import java.net.ServerSocket;

public class MainClass {
    public static int DEFAULT_PORT = 6789;
    public static void main(String[] args) {
        int port;
        try{
            port = Integer.parseInt(args[0]);
        }catch(RuntimeException e){
            port = DEFAULT_PORT;
        }
        Server server = new Server(port);
        System.out.println("Listening for connections on port " + port);
        server.esegui();

    }
}
