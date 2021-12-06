import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ServerSender {
    private static final int SERVER_PORT = 6800;
    private int port;
    private String address;

    public ServerSender(int port, String address) {
        this.port = port;
        this.address = address;
    }

    public void esegui(){
        try(DatagramSocket ms = new DatagramSocket(SERVER_PORT)){
            InetAddress groupAddress = InetAddress.getByName(address);
            if(!groupAddress.isMulticastAddress()){
                throw new IllegalArgumentException(groupAddress + "is not a multicast address");
            }
            InetSocketAddress group = new InetSocketAddress(groupAddress, port);



            //get current time
            while(true) {
                String time = String.valueOf(System.currentTimeMillis());
                //creazione datagram packet
                DatagramPacket packet = new DatagramPacket(time.getBytes(StandardCharsets.UTF_8), time.length(), groupAddress, port);
                //send packet
                ms.send(packet);
                Thread.sleep(5000);
            }


        }catch(IOException | InterruptedException e){}

    }
}
