import javax.xml.crypto.Data;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class Server{

    public static void main(String[] args) {

            try (DatagramSocket dgs = new DatagramSocket(901)) {
            byte[] buffer = new byte[4];
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            while(true){
                dgs.receive(receivedPacket);
                String byteToString = new String(receivedPacket.getData(), 0, receivedPacket.getLength(), "US-ASCII");
                System.out.println(byteToString);
                InetAddress client = receivedPacket.getAddress();
                int port = receivedPacket.getPort();
                DatagramPacket sendPong = new DatagramPacket("pong".getBytes(StandardCharsets.UTF_8), 4, client, port);
                dgs.send(sendPong);
            }
        }catch(Exception e){}
    }


}
