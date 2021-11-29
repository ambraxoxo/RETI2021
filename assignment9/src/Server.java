import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class Server {
    public static int DEFAULT_PORT = 6789;

    public static void main(String[] args) {
        int port;
        try{
           port = Integer.parseInt(args[0]);
        }catch(Exception e){
            port = DEFAULT_PORT;
        }

        System.out.println("Listening for connections on port " + port);

        ServerSocketChannel serverChannel;
        Selector selector = null;

        try{
            serverChannel = ServerSocketChannel.open();
            ServerSocket ss = serverChannel.socket();
            InetSocketAddress address = new InetSocketAddress(port);
            ss.bind(address);
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        }catch(IOException e){
            System.exit(-1);
        }

        while(true){
            try{
                selector.select();
            }catch(IOException e){
                break;
            }
        }

        Set<SelectionKey> readyKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = readyKeys.iterator();

        while(iterator.hasNext()){
            SelectionKey key = iterator.next();
            iterator.remove();
            //rimuove la key solo dal selected set
            try{
                if(key.isAcceptable()){
                    //accetto la connessione registro la key (key2) in lettura per leggere dal client (?)
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel client = server.accept();
                    System.out.println("accepted connection from" + client);
                    client.configureBlocking(false);


                }else if(key.isReadable()){
                    SocketChannel sChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int rBytes = sChannel.read(buffer);
                    String msg;

                    if(rBytes > 0){
                        buffer.flip();
                        Charset charset = StandardCharsets.UTF_8;
                        CharsetDecoder decoder = charset.newDecoder();
                        CharBuffer charBuffer = decoder.decode(buffer);
                        msg = charBuffer.toString();
                        System.out.println("received message" + msg);
                        if(msg.length() > 0){
                            msg += " - Echoed by server";
                            SocketChannel sChannel2 = (SocketChannel) key.channel();
                            ByteBuffer buffer2 = ByteBuffer.wrap(msg.getBytes());
                            sChannel2.write(buffer2);
                        }
                    }
                }
            }catch(IOException e){
                key.cancel();
            }
        }
    }
}
