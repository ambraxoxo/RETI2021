/* riceve in input un filepath che individua una directory D
* stampa il cntenuto di quella directory e ricorsivamente di quelle all'interno
*
* 1 - attiva un  thread produttore e k thread consumatori
* 2 - il produttore comunica con i consumatori tramite una coda
* 3 - il produttore visita ricorsivamente la directory data e  eventualmente tutte le sottodirectory e
*   mette nella coda il nome di ogni directory individuata
* 4 - i consumatori prelevano dalla coda i nomi delle directory e stampano il loreo contenuto
* 5 - coda realizzata con LinkedList (nota non è thread safe) va sincronizzata tutta?*/

import java.io.File;
import java.util.LinkedList;

public class MainClass {
    public static void main(String[] args) {
        int c_num = 0;
        String basedir = null;
        try {
            basedir = args[0];
            c_num = Integer.parseInt(args[1]);
        }catch(Exception e){
            System.out.println("errore passaggio argomenti");
        }

        File startDir = new File(basedir);
        //LinkedList<String> list = new LinkedList<>();
        PathList lista = new PathList();
        if(!startDir.exists()){
            System.out.println("Il file iniziale non esiste");
            System.exit(-1);
        }

        if(!startDir.isDirectory()){
            System.out.println("Il file iniziale non è una directory");
            System.exit(-1);
        }
        Producer prod = new Producer(startDir, lista);
        prod.run();

        for(int i = 0; i < c_num; i++){
            Consumer c = new Consumer(lista);
            c.run();
        }

    }
}
