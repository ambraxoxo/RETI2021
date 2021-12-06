public class ServerMain {
    private final static int DEFAULT_PORT = 6789;

    public static void main(String[] args) {
        int serverPort;
        String address = args[0];
        try{
            serverPort = Integer.parseInt(args[1]);
        }catch(Exception e){
            serverPort = DEFAULT_PORT;
        }

        Server server = new Server(serverPort, address);
        server.esegui();
    }
}
