import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void esegui(){
        ServerSocketChannel serverChannel;
        Selector selector;
        try{
            serverChannel  = ServerSocketChannel.open();
            ServerSocket ss = serverChannel.socket();
            InetSocketAddress address = new InetSocketAddress(port);
            ss.bind(address);
            serverChannel.configureBlocking(false);
            //potenzialmente non serve sti due dopo
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        }catch(IOException e){
            return;
        }

        while(true){
            try{
                selector.select();
            }catch(IOException e){
                break;
            }
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator <SelectionKey> iterator = readyKeys.iterator();

            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                //rimuove la chiave dal selectedSet, ma non dal registered Set
                try{
                    if(key.isAcceptable()){
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        System.out.println("Accepted connection from " + client);
                        client.configureBlocking(false);
                        SelectionKey key2 = client.register(selector, SelectionKey.OP_WRITE);
                        ByteBuffer output = ByteBuffer.allocate(12);
                        output.put("HelloClient".getBytes(StandardCharsets.UTF_8));
                        output.flip();
                        key2.attach(output);
                        client.write(output);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try{
                    key.channel().close();
                }catch(IOException e){}
            }
        }
    }
}
