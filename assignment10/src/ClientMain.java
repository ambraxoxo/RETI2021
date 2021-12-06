import java.util.concurrent.TimeoutException;

public class ClientMain {
    private static int port = 6789;
    private static String address = "239.255.1.3";

    public static void main(String[] args) {

        if(args.length != 2){
            System.out.println("Usage: java ClientMain [address] [port]");
            System.exit(-1);
        }

        try{
            port = Integer.parseInt(args[1]);
        }catch(Exception e){
            port = 6789;
        }
        address = args[0];
        TimeClient client = new TimeClient(port, address);
        try {
            client.esegui();
        }catch(IllegalArgumentException e){
            System.out.println(e.getMessage());
            System.out.println("oof");
            System.exit(-1);
        }
    }
}
