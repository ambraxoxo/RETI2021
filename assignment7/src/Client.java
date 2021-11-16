import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

public class Client {

    public static void main(String[] args) {
        ArrayList<Long> RTTList = new ArrayList<>(10);
        long timeSent, timeReceived;
        InetAddress address = null;
        int serverPort = 0;
        try{
            address = InetAddress.getByName(args[0]);
        }catch(UnknownHostException e){
            System.out.println("ERR -arg 0");
            System.exit(-1);
        }
        try{
            serverPort = Integer.parseInt(args[1]);
        }catch(Exception e){
            System.out.println("ERR -arg 1");
            System.exit(-1);
        }

        try(DatagramSocket clientSocket = new DatagramSocket(1500)){
            //timeout della receive
            clientSocket.setSoTimeout(2000);
            for(int i = 0; i < 10; i++){
                //costruisco messaggio ping con timestamp e seqno
                timeSent = System.currentTimeMillis();
                String toSend = "PING " + i + " " + timeSent;
                //System.out.println(toSend);
                byte[] sendBuffer = (toSend.getBytes(StandardCharsets.UTF_8));
                DatagramPacket sendMessage = new DatagramPacket(sendBuffer, sendBuffer.length, address, serverPort);
                //mando datagram al server
                clientSocket.send(sendMessage);
                byte[] receiveBuffer = new byte[sendBuffer.length];
                DatagramPacket receiveMessage = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                //ricevo echo datagram dal server
                try {
                    clientSocket.receive(receiveMessage);
                    timeReceived = System.currentTimeMillis();
                    RTTList.add(timeReceived - timeSent);
                    String byteToString = new String(receiveMessage.getData());
                    System.out.println(byteToString + " RTT: " + (timeReceived - timeSent + " ms"));
                }catch(SocketTimeoutException e){
                    System.out.println("*");
                }
            }
        }catch(SocketException e){
            System.out.println("Errore nella creazione del clientSocket");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("errore send o receive");
        }


        long max = Collections.max(RTTList);
        long min = Collections.min(RTTList);
        double avg = RTTList.stream().mapToDouble(val -> val).average().orElse(0.0);
        System.out.println("---- PING Statistics ----");
        System.out.println("10 Packets transmitted, " + RTTList.size() + " packets received " + (10 - RTTList.size()) + "0% packet loss");
        System.out.println("round - trip (ms) min/avg/max = " + min + "/" + String.format("%.2f", avg) + "/" + max);

    }
}
