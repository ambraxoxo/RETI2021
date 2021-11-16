import java.util.*;

public class Task implements Runnable{
    private int id;

    public Task(int id){
        this.id = id;
    }
    public void run(){
        System.out.println("Viaggiatore " + id + ": sto acquistando un biglietto");

        try{
            Long duration = (long)(Math.random()*1000);
            Thread.sleep(duration);
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        System.out.println("Viaggiatore " + id + ": ho acquistato il biglietto");


    }
}
