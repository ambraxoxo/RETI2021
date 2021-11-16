import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MainClass {


    public static void main(String args[]){
        long time1, time2;
        CounterRW count = new CounterRW();
        ExecutorService pool = Executors.newFixedThreadPool(10);
        Writer write;
        Reader read;

        time1 = System.currentTimeMillis();
        for(int i = 0; i < 20; i++){
            write = new Writer(count);
            pool.submit(write);
            read = new Reader(count);
            pool.submit(read);
        }
        
        pool.shutdown();
        time2 = System.currentTimeMillis();
        System.out.println("tempo: " + (time2 - time1));
    }
}
