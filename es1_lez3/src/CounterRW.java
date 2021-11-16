import java.util.concurrent.locks.*;

public class CounterRW {

    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock read = readWriteLock.readLock();
    private Lock write = readWriteLock.writeLock();
    private int contatore = 0;


    public void increment(){

        try {
            write.lock();
            contatore++;
        }finally{
            write.unlock();
        }

    }

    public int get(){
        try {
            read.lock();
            return contatore;
        }finally {
            read.unlock();
        }
    }
}