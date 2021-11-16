public class Task implements Runnable{
    //id per stamoare il numero del cliente
    private int id;

public Task(int id){
    this.id = id;

}
//override del metodo run di Runnable, la Task stampa numero del cliente, simula l'interazione con l'emettitrice tramite una sleep di durata casuale e stampa la durata
    @Override
    public void run(){
        System.out.println("Cliente " + id);
        try{
            Long duration = (long)(Math.random()*1000);
            System.out.println("Durata task del cliente " + id + ": " + duration);
            Thread.sleep(duration);
        }catch(InterruptedException e){
            e.printStackTrace();
            return;
        }
    }


}
