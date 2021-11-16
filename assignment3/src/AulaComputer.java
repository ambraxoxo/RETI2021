import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AulaComputer {
    //lock per gestire l'accesso al laboratorio (viene bloccata dai professori con writelock
    public static ReentrantReadWriteLock aulaLock = new ReentrantReadWriteLock();
    //array di lock per gestire la lock sul pc
    public static ArrayList<ReentrantReadWriteLock> computers = new ArrayList<>(20);

    //inizializzo l'array di lock per i singoli computer
    public AulaComputer(){
        for(int i = 0; i < 20; i++){
            computers.add(new ReentrantReadWriteLock());
        }
    }

}
