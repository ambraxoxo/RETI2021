import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server{
    public static void main(String[] args) {
        int port = 0;
        try{
            port = Integer.parseInt(args[0]);
        }catch(Exception e){
            System.out.println("ERR -arg 0");
            System.exit(-1);
        }

        try(DatagramSocket serverSocket = new DatagramSocket(port)){
            for(int i = 0; i < 10; i++) {
                byte[] buffer = new byte[100];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(receivePacket);
                String byteToString = new String(receivePacket.getData(), 0, receivePacket.getLength(), "US-ASCII");
                //System.out.println(byteToString);
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                if(!dropped()) {
                    int duration = (int) (Math.random()*300);
                    Thread.sleep(duration);
                    receivePacket.setAddress(clientAddress);
                    receivePacket.setPort(clientPort);
                    serverSocket.send(receivePacket);
                    System.out.println(clientAddress + ":" + clientPort + "> " + byteToString + " ACTION: delayed " + duration + " ms");
                }else{
                    System.out.println(clientAddress + ":" + clientPort + "> " + byteToString + " ACTION: not sent");
                }
            }

        }catch(SocketException e){
            System.out.println("impossibili creare la socket del server sulla porta" + port);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean dropped(){
        if(((Math.random()*100) + 1) <= 25) return true;
        else return false;
    }
}