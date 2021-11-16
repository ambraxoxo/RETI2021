import java.util.concurrent.*;

public class Power implements Callable <Double> {
    private Double n;
    private int esponente;

    public Power (Double n, int esponente){
        this.n = n;
        this.esponente = esponente;
    }


    public Double call(){
        System.out.println("Esecuzione " + n + "^" + esponente + " in " + Thread.currentThread().getId());
        return Math.pow(n, esponente);
    }
}
