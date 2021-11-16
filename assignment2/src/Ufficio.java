import java.util.concurrent.*;

//ufficio una classe java che crea un threadpool di dimensione uguale al numero degli sportelli
public class Ufficio {
    //dichiarazione threadpool
    private ExecutorService pool;
    //dichiarazione coda per la sala principale
    private ArrayBlockingQueue<Task> sala;
    private int dim;

    public Ufficio(int dim) {
        //creazione nuovo threadpool
        pool = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
        sala = new ArrayBlockingQueue(dim);
        this.dim = dim;
    }

    //crea la sala principale (sala) con capacità dim e gestisce il passaggio dei clienti (task) da una coda all'altra
    public void esegui() {
        //aggiungo alla coda (sala) ogni task (cliente)
        for (int i = 0; i < dim; i++) {
            sala.add(new Task(i));
        }
        //finché ho elementi nella coda
        while (!sala.isEmpty()) {
        //provo a eseguire una task della coda, se la coda della pool non è piena non genera l'eccezione quindi tolgo la task dalla sala
            try {
                pool.execute(sala.peek());
                sala.remove();
            } catch (RejectedExecutionException e) {}
        }
        //shutdown della pool
        pool.shutdown();
        try{
            if(!pool.awaitTermination(60, TimeUnit.SECONDS)){
                pool.shutdownNow();
            }
        }catch(InterruptedException e){
            pool.shutdownNow();

        }
        return;



    }
}
