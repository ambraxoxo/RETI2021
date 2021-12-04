import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;

public class Client {
    public static int DEFAULT_PORT = 6789;
    private static BufferedReader userInputReader = null;

    public static void main(String[] args) throws Exception {
        int port;
        if(args.length == 0){
            System.out.println("Usage: java assignment9 host [port]");
        }

        try{
            port = Integer.parseInt(args[1]);
        }catch(Exception e){
            port = DEFAULT_PORT;
        }
        try(SocketChannel client = SocketChannel.open()){
            Selector selector = Selector.open();
            SocketAddress address= new InetSocketAddress(args[0], port);
            client.configureBlocking(false);
            client.connect(address);
            client.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            userInputReader = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                if(selector.select() > 0){
                    boolean doneStatus = processReadySet(selector.selectedKeys());
                    if(doneStatus) break;

                }
            }
           // SocketChannel client = SocketChannel.open(address);
        }catch(IOException e){
            System.exit(-1);
        }

    }

    public static boolean processReadySet(Set readySet) throws Exception {
        SelectionKey key = null;
        Iterator iterator = null;
        iterator = readySet.iterator();

        while(iterator.hasNext()){
            key = (SelectionKey) iterator.next();
            iterator.remove();

            if(key.isConnectable()){
                boolean connected = processConnect(key);
                if(!connected){
                    return true;
                }
            }
            if(key.isReadable()){
                String msg = processRead(key);
                System.out.println("[Server]: " + msg);
            }
            if(key.isWritable()){
                String msg = getUserInput();
                if(msg.equalsIgnoreCase("exit")){
                    return true;
                }
                processWrite(key,msg);
            }

        }
        return false;
    }

    public static boolean processConnect(SelectionKey key){
        SocketChannel channel = (SocketChannel) key.channel();

        try{
            while(channel.isConnectionPending()){
                channel.finishConnect();
            }
        }catch(IOException e){
            key.cancel();
            return false;
        }

        return true;
    }

    public static String processRead(SelectionKey key) throws Exception {
        SocketChannel sChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        sChannel.read(buffer);
        buffer.flip();
        Charset charset = Charset.forName("UTF-8");
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer charBuffer = decoder.decode(buffer);
        String msg = charBuffer.toString();
        return msg;
    }
    public static void processWrite(SelectionKey key, String msg)
            throws IOException {
        SocketChannel sChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
        sChannel.write(buffer);
    }

    public static String getUserInput() throws IOException {
        String promptMsg = "Please enter a message(Bye to quit): ";
        System.out.print(promptMsg);
        String userMsg = userInputReader.readLine();
        return userMsg;
    }
}
