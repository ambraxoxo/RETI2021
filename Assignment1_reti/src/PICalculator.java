import java.lang.Thread;
import java.lang.Math;

public class PICalculator implements Runnable{
    //accuracy come attributo della classe per usarlo nella funzione run
    private final float accuracy;

    public PICalculator( float accuracy) {
        this.accuracy = accuracy;
    }


    public void run(){
        float pi = 4, div = 3;
        int  count = 1;
        //finché il thread non viene interrotto dal main perché supera il tempo massimo
        while(!Thread.interrupted()){
            //se ho raggiunto l'accuracy richiesta (in valore assoluto) mi interrompo
            if((Math.abs(pi - Math.PI)) <= Math.abs(this.accuracy)){
                if (!Thread.interrupted()) {
                    System.out.println("Accuracy ottenuta a " + pi);
                    Thread.currentThread().interrupt();
                    return;
                }

            }
            //calcolo di PI con la succ essione
            if(count % 2 == 0){
                pi = pi + 4/div;
            }else{
                pi = pi - 4/div;
            }
            count++;
            div += 2;
        }


    }
}
