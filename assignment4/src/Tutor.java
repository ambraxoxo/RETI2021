
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.TimeUnit;
//i professori hanno priorità su tutti, i tesisti sugli studenti
// nessuno può essere interrotto mentre sta usando il computer
//il programma riceve in ingresso il numero di studenti, tesisti e professori che usano il lab e attiva un thread per ogni utente.
//ogni utente accede k volte al laboratorio con k generato casualmente
//intervallo tra un accesso e il successivo e la permanenza con sleep random
//il tutor coordina gli accessi al laboratorio

public class Tutor {
    private int studenti;
    private int prof;
    private int tesisti;



    public Tutor(int studenti, int prof, int tesisti){
        this.studenti = studenti;
        this.prof = prof;
        this.tesisti = tesisti;
    }

    public void esegui(){

        ExecutorService pool = Executors.newCachedThreadPool(); //newFixedThreadPool(studenti + prof + tesisti);

        AulaComputer lab = new AulaComputer();
        int p = prof, s = studenti, t = tesisti;
        System.out.println(p + " " + s + " " + t);
        while(p != 0 || s != 0 || t != 0){
            if(p != 0){
                //nuovo prof, mando in threadpool
                pool.execute(new Professore(lab));
                p--;

            }
            if( s!= 0){
                // nuovo studente mando in threadpool
                pool.execute(new Studente(lab));
                s--;
            }
            if(t != 0){
                //nuovo tesista threadpool
                pool.execute(new Tesista(lab));
                t--;
            }
        }
        //shudown della pool con gestione terminazione thread
        pool.shutdown();
        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        return;

    }
}
