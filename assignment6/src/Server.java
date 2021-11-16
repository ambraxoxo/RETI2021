import java.io.*;
import java.net.ServerSocket;
import java.net.*;

public class Server {
    public static void main(String[] args) {

        try(ServerSocket server = new ServerSocket(1500)){


            while(true) {
                String message, response;
                //accetto richieste
                Socket sock = server.accept();
                System.out.println("accettata connessione");
                //inizializzazione reader e writer
                BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));



                message = reader.readLine();
                System.out.println(message);

            }

        }catch(Exception e){
            System.out.println("impossibile aprire la socket");
        }
    }

    public void sendResponse(String message, Socket sock){
        File testFile = new File("./src/howl.gif");
        FileInputStream inputFile;
        OutputStream writer = sock.getOutputStream();

        try{
            inputFile = new FileInputStream(testFile);
        }catch(Exception e) {
            System.out.println("Errore nell'inizializzazione file");
            return;
        }
        writer.write("""
                        HTTP/1.1 200 OK\r
                        Server: HTTP_Java_Server\r
                        Content-Type: html\r
                        \r
                        """.getBytes());
        byte[] responseContent = new byte[(int) testFile.length()];

        inputFile.read(responseContent);
        writer.write(responseContent);

    }
}
