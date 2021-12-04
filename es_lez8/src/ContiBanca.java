import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.*;

public class ContiBanca {
    public final static int nBytes = 500;
    public final static int nConti = 20;

    static List<Conto> clienti = new ArrayList<>();
    private static List<String> nomi = new ArrayList<>();
    private static List<String> cognomi = new ArrayList<>();

    public static void main(String[] args) {
    //FileChannel legge/scrive dati su un file
    //FileChannel sono bloccanti e thread-safe
        FileChannel outChannel = null;

        try{
            outChannel = FileChannel.open(Paths.get("conti.json"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        }catch(IOException e){
            System.out.println("File not found 24 contibanca");
            System.exit(-1);
        }

        ByteBuffer buffer = ByteBuffer.allocate(nBytes);

        try{
            String path1 = args[0];
            String path2 = args[1];

            File nomiFile = new File(path1);
            File cognomiFile = new File(path2);

            toList(nomiFile, nomi);
            toList(cognomiFile, cognomi);
        }catch(Exception e) {
            System.out.println("Errore argomenti, inserire path_nomi path_cognomi");
            System.exit(-1);
        }
    }

    private static Conto creaConto(){
        Random r = new Random();
        //nomi e cognomi random dalla lista di nomi e cognomi

    }

    private static void toList(File f, List<String> list) throws IOException{
        BufferedReader readBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        String s = readBuffer.readLine();
        while (s != null){
            list.add(s);
            s = readBuffer.readLine();
        }

    }

    private static String generateDate() {
        Random r = new Random();

        int mm = Math.abs(r.nextInt(11) + 1); // genero numero nel range [1-12]
        int aa = Math.abs(r.nextInt(2) + 2019); // genero numero nel range [2019-2021]
        int gg;
        if(mm == 2 && aa % 4 == 0) // anno bisestile
            gg = Math.abs((r.nextInt() % 29) + 1); // genero numero nel range [1-29]
        else
            gg = Math.abs((r.nextInt() % 28) + 1); // genere numero nel range [1-28]

        // costruisco la stringa corrispondente alla data generata
        String date = "";

        if(gg < 10) date += "0" + gg + "/";
        else date += gg + "/";

        if(mm < 10) date += "0" + mm + "/";
        else date += mm + "/";

        date += aa;

        return date;
    }
}
