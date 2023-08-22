import java.io.IOException;
import java.net.*;

import java.util.concurrent.LinkedBlockingQueue;

public class NotificationThread extends Thread{
    private final MulticastSocket socket;
    private final InetSocketAddress group;
    private final NetworkInterface netIf;
    private final LinkedBlockingQueue<String> notifications;

    public NotificationThread(String address, int port) throws IOException {
        socket = new MulticastSocket(port);
        if(!InetAddress.getByName(address).isMulticastAddress()){
            throw new IllegalArgumentException(address + " is not a multicast address");
        }
        group = new InetSocketAddress(InetAddress.getByName(address), port);
        netIf = NetworkInterface.getByName("wlan1");
        notifications = new LinkedBlockingQueue<>();
    }

    /**
     * Stampa le notifiche che sono state condivise dagli altri utenti
     */

    public void printNotifications(){
        if(notifications.isEmpty()){
            System.out.println("Non ci sono condivisioni da visualizzare.");
        }

        for(String d : notifications){
            String[] notifArray = d.split("\n");
            if(!notifArray[0].equals(ClientMain.getUsername()))
                System.out.println(d);
            else if(notifications.size() == 1) System.out.println("Non ci sono notifiche da visualizzare");
        }
        notifications.clear();
    }

    /**
     * data: stringa che contiene il risultato di un utente che ha condiviso.
     * Il thread riceve con un timeout di 1 secondo i DatagramPacket dalla socket multicast,
     * ne estrapola i dati e li aggiunge alla coda di notifiche
     */
    @Override
    public void run() {
    try {
        socket.joinGroup(group, netIf);
        socket.setSoTimeout(1000);
    } catch (IOException e) {
        e.printStackTrace();
    }

        while(!this.isInterrupted()){
            byte[] buff = new byte[1024];
            String data = null;
            DatagramPacket packet = new DatagramPacket(buff, buff.length);
            try {
                socket.receive(packet);
                data = new String(packet.getData(), 0, packet.getLength());
            } catch (IOException e) {
            }
            try {
                notifications.add(data);
            }catch(Exception e){}
        }
    try {
        socket.leaveGroup(group, netIf);
    } catch (IOException e) {
        e.printStackTrace();
    }
    socket.close();
    }
}
