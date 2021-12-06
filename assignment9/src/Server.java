import java.io.IOException;
import java.net.InetSocketAddress;
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
import java.util.Scanner;
import java.util.Set;

public class Server {
    private int port;
    private String address;

    public Server(int port, String address) {
        this.port = port;
        this.address = address;
    }

    public void esegui(){
        try(ServerSocketChannel s_channel = ServerSocketChannel.open()){
            s_channel.socket().bind(new InetSocketAddress(port));
            s_channel.configureBlocking(false);
            Selector sel = Selector.open();
            s_channel.register(sel, SelectionKey.OP_ACCEPT);
            System.out.println("Il server attende connessioni sulla porta: " + port);

            while(true){
                if(sel.select() == 0)
                    continue;
                Set<SelectionKey> selectedKeys = sel.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while(iter.hasNext()){
                    SelectionKey key = iter.next();
                    iter.remove();
                    if(key.isAcceptable()){
                        registerKey(sel, key);
                    }
                    if(key.isReadable()){
                        sendMessage(key);
                    }
                }
            }

        }catch(Exception e){}
    }
    private void registerKey(Selector sel, SelectionKey key) throws IOException{
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel c_channel = server.accept();
        c_channel.configureBlocking(false);
        System.out.println("Accepted connection from: " + c_channel.getRemoteAddress());
        c_channel.register(sel, SelectionKey.OP_READ);
    }
    public void sendMessage(SelectionKey key) throws Exception{
        SocketChannel c_channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytes = c_channel.read(buffer);
        String msg;

        if (bytes > 0) {
            buffer.flip();
            Charset charset = StandardCharsets.UTF_8;
            CharsetDecoder decoder = charset.newDecoder();
            CharBuffer charBuffer = decoder.decode(buffer);
            msg = charBuffer.toString();
            msg += " [Echoed by server]";
            echo(msg, key);
        }

        c_channel.close();
        key.cancel();
    }

    private void echo(String message, SelectionKey key) throws IOException {
        SocketChannel c_channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
        c_channel.write(buffer);


    }
}
