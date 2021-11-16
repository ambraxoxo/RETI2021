import java.util.ArrayList;
import java.util.concurrent.*;


public class Main  {


    public static void main(String args[]){
        Double num = Double.parseDouble(args[0]), res = 0.0;
        int esponente = 50;
        ArrayList<Future<Double>> list = new ArrayList();
        ExecutorService pool = Executors.newCachedThreadPool();
        for(int i = 2; i <= esponente; i++){
            Power power = new Power(num, i);
            list.add(i-2, pool.submit(power));
        }

        for(Future<Double> f : list){
            try {
                // System.out.println(list.get(i).get());
                res += f.get(1000L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
       /* for(int i = 0; i <= (esponente - 2); i++){
            try {
               // System.out.println(list.get(i).get());
               res += f.get(1000L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }*/
        System.out.println(res);

        pool.shutdown();
        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }

    }
}
