//i tesisti richiesono l'uso esclusivo di un solo computer identificato dall'indice i che è quello che ha il software che gli serve


import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Tesista implements Runnable{
    //readlock per l'accesso al laboratorio
    private ReentrantReadWriteLock.ReadLock lab = AulaComputer.aulaLock.readLock();
    //writelock per avere priorità sullo studente al singolo pc di id: id
    private ReentrantReadWriteLock.WriteLock pc;
    private int id;


    public Tesista(){
        int id = (int)(Math.random()*20);
        this.id = id;
        pc = AulaComputer.computers.get(id).writeLock();
    }

    public void run(){
        long duration = (long)(Math.random()*1000);
        int k = (int)(Math.random()*5) + 1;

        //k utilizzi del laboratorio
        for(int i = 0; i < k; i++) {
            try {
                //readlock del lab, se è in write da un professore il thread aspetterà che a risorsa sia disponibile
                lab.lock();
                //writelock sul singolo pc, se è in readlock da uno studente asspetta che la risorsa sia disponibile
                pc.lock();
                //simula utilizzo
                System.out.println("Tesista " + Thread.currentThread().getId() + " sta usando pc " + id);
                Thread.sleep(duration);
            }catch(InterruptedException e){}
            finally{
                //sblocco risorse
                pc.unlock();
                lab.unlock();
            }

        }
    }
}
