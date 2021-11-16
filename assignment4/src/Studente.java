//gli studenti richiedono l'uso di un qualsiasi computer

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.*;

public class Studente implements Runnable {
    AulaComputer lab;
    //Read lock per l'aula computer per occuparla solo quando non c'è il professore
    // private ReentrantReadWriteLock.ReadLock lab = AulaComputer.aulaLock.readLock();
    //readlock per il computer, non writelock per non avere la stessa priorità del tesista
    //private ArrayList<ReentrantReadWriteLock> pc = AulaComputer.computers ;


    public Studente(AulaComputer lab) {
        this.lab = lab;
    }

    public void run() {
        //duration dello sleep
        long duration = (long) (Math.random() * 1000);
        //readLock da usare per i pc
        // ReentrantReadWriteLock.ReadLock pc_lock = new ReentrantReadWriteLock().readLock();
        Computer pc;
        int k = (int) (Math.random() * 5) + 1;
       // System.out.println(k + " studente " + Thread.currentThread().getId());
        //variabile per controllare quando ho finito questo utilizzo del laboratorio

        //k volte che lo studente usa il laboratorio
        for (int j = 0; j < k; j++) {
            pc = lab.studentPcStartUse();

                        lab.pcStartUse(pc);

                    try {
                        System.out.println("Studente " + Thread.currentThread().getId() + " sta usando pc " + AulaComputer.computers.indexOf(pc));
                        Thread.sleep(duration);
                    } catch (InterruptedException e) {
                    }
                    lab.pcEndUse(pc);


                }
            }




}

               /* done = false;
            i = 0;
            try{
                lab.lock();
                while (!done) {
                    pc_lock = pc.get(i).readLock();
                    //cerco un pc che non sia occupato da un altro studente
                    if (pc.get(i).getReadLockCount() == 0) {
                        if(pc_lock.tryLock()){
                            System.out.println("Studente " + Thread.currentThread().getId() + " sta usando pc " + (i));
                            Thread.sleep(duration);
                            done = true;
                        }
                    }
                    i = (i + 1) % 20;
                }
            }catch(InterruptedException e){}
            finally{
                pc_lock.unlock();
                lab.unlock();
            }

        }*/