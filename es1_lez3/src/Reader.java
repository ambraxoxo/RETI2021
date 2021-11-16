public class Reader implements Runnable{
    CounterRW count;

    public Reader(CounterRW count){
        this.count = count;
    }

    public void run(){
        System.out.println(count.get());
    }
}