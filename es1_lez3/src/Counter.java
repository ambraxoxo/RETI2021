import java.util.concurrent.locks.*;

public class Counter {

    private final Lock countLock = new ReentrantLock();
    private int contatore = 0;


    public void increment(){
        try {
            countLock.lock();
            contatore++;
        }finally{
            countLock.unlock();
        }
    }

    public int get(){
        try {
            countLock.lock();
            return contatore;
        }finally {
            countLock.unlock();
        }
    }
}