public class ServerMain {
    private static int port = 6789;
    private static String address = "239.255.1.3";
    public static void main(String[] args) {
        //controllo argomenti
        if(args.length != 2){
            System.out.println("usage: java Server [address] [port]");
            System.exit(-1);
        }
        try {
            port = Integer.parseInt(args[1]);
        }catch(Exception e){
            port = 6789;
        }
        address = args[0];
        ServerSender server = new ServerSender(port, address);
        try{
            server.esegui();
        }catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }
}
