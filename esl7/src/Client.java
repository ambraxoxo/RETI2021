import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class Client{
    public static void main(String[] args) {


        try(DatagramSocket clientSocket = new DatagramSocket(6789)){
            while(true) {
                byte[] buffer = "ping".getBytes("US-ASCII");
                InetAddress address = InetAddress.getByName("127.0.0.1");
                DatagramPacket message = new DatagramPacket(buffer, buffer.length, address, 901);
                clientSocket.send(message);
                byte[] rMessage = new byte[4];
                DatagramPacket received = new DatagramPacket(rMessage, rMessage.length);
                clientSocket.receive(received);
                System.out.println(new String(received.getData()));
            }
        }catch(Exception e){}
    }
}
