import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class TimeClient {
    private int port;
    private String address;

    public TimeClient(int port, String address) {
        this.port = port;
        this.address = address;
    }

    public void esegui(){
        try(MulticastSocket ms = new MulticastSocket(port)){
            InetSocketAddress group = new InetSocketAddress(InetAddress.getByName(address), port);
            if(!InetAddress.getByName(address).isMulticastAddress()){
                throw new IllegalArgumentException(address + " is not a multicast address");
            }
            NetworkInterface netIf = NetworkInterface.getByName("wlan1");
            ms.joinGroup(group, netIf);
            for(int i = 0; i < 10; i++) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                ms.receive(packet);
                String s = new String(packet.getData(), packet.getOffset(), packet.getLength());
                //SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyy HH:mm");
                Instant instant = Instant.ofEpochMilli(Long.parseLong(s));
                System.out.println(instant);
            }
            ms.leaveGroup(group, netIf);

        }catch(IOException e){
            System.out.println("oof 2");
            System.exit(-1);
        }
    }
}
