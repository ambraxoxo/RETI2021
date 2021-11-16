import java.io.*;
import java.net.*;
import java.nio.Buffer;
import java.util.LinkedList;

public class Server {

    public static LinkedList<String> generate_graph(int vert){
        LinkedList<String> graph = new LinkedList<String>();
        for (int i = 0; i < vert; i++){
            graph.add(Integer.toString((int)(Math.random()*vert)) + " - " + Integer.toString((int)(Math.random()*vert)));
        }
        return graph;
    }

    public static void print_to_file(String path, LinkedList<String> graph) throws IOException {
        OutputStream data_file = new FileOutputStream(path);
        for(String arc: graph){
            data_file.write((arc + "\n").getBytes());
        }
        data_file.close();
    }

    public static void main(String[] args) {
       // ServerSocket server;
        boolean done = false;
        try {
            ServerSocket server = new ServerSocket(901);
            int i = 0;
            while(!done){
                Socket sock = server.accept();


                LinkedList<String> graph = generate_graph(10);
                print_to_file("./data" + i + ".txt", graph);

              //  FileInputStream file = new FileInputStream("./data" + i++ + ".txt");
                BufferedWriter send = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
                send.write("ciao");
                send.flush();
                sock.close();
            }

            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
