public class Writer implements Runnable{
    CounterRW count;

    public Writer(CounterRW count){
        this.count = count;

    }

    public void run(){
        count.increment();


    }
}
