
import java.util.*;

public class MainClass {

    public static void main(String[] args){
        float accuracy;
        long max_wait;
        //input di accuracy e tempo massimo
        Scanner input = new Scanner(System.in);
        System.out.println("Inserire accuracy");
        accuracy = input.nextFloat();
        System.out.println("Inserire tempo massimo di attesa");
        max_wait = input.nextInt();
        //creo il thread per calcolare PI
        PICalculator piCalc = new PICalculator(accuracy);
        Thread thread = new Thread(piCalc);
        //avvio il thread
        thread.start();
        //aspetto al massimo max_wait che il thread PI finsica
        try {
            thread.join(max_wait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //se il thread non ha finito lo interrompo
        if(thread.isAlive()) {
            thread.interrupt();
            System.out.println("Tempo massimo superato");
        }


    }


}
