public class Server {
    private static int port;
    private static String address;
    public static void main(String[] args) {
        //controllo argomenti
        if(args.length != 2){
            System.out.println("usage: java Server [address] [port]");
            System.exit(-1);
        }

    }
}
