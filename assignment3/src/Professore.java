//i professori accedono in modo esclusivo a tutto il laboratorio

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Professore implements Runnable{
    private ReentrantReadWriteLock.WriteLock lab = AulaComputer.aulaLock.writeLock();

    public void run(){
       //duration: durata della sleep del professore che simula l'utilizzo del laboratorio
        //k: volte che il professore accede al laboratorio

        long duration = (long)(Math.random()*1000);
        int k = (int)(Math.random()*5) + 1;

        //accedo al laboratorio k volte
        for(int i = 0; i < k; i++) {
            //chiamo lock sul laboratorio (se la lock fallisce il thread si mette in attesa della risorsa, nel nostro caso
            //che studenti e tesisti che hanno la readlock la sblocchino
            //dopo aver lockato simula l'utilizzo poi sblocca il lab
           try {
               lab.lock();
               System.out.println("Professore " + Thread.currentThread().getId() + " usa il laboratorio");
               Thread.sleep(duration);
           }catch(InterruptedException e){}
           finally{
               lab.unlock();
           }
        }


    }
}
