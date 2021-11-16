import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
//array di object per acquisire la risorsa synchronized e oggetto aula computer per il professore?

public class AulaComputer {
    //lock per gestire l'accesso al laboratorio (viene bloccata dai professori con writelock
    public static Object aula = new Object();
    //public static ReentrantReadWriteLock aulaLock = new ReentrantReadWriteLock();
    //array di lock per gestire la lock sul pc

    public static ArrayList<Computer> computers = new ArrayList<>(20);
    //public static ArrayList<ReentrantReadWriteLock> computers = new ArrayList<>(20);
    private boolean using;
    private int pc_in_use;

    //inizializzo l'array di lock per i singoli computer
    public AulaComputer(){
        for(int i = 0; i < 20; i++){
            computers.add(new Computer());
            this.using = false;
            this.pc_in_use = 0;
        }
    }
    synchronized void tesistaPcStartUse(Computer pc){
        int i = 0;
        if(using || pc.isUsing() || pc_in_use == 20){ pc.addTes();}
        while(using || pc.isUsing() || pc_in_use == 20){
            try{
                wait();
            }catch(InterruptedException e){}
        }
        if(!pc.t_queue.isEmpty()) {
            pc.decTes();
        }
        pc.setUsing(true);
        pc_in_use++;
    }

    synchronized void tesPcEndUse(Computer pc){
        pc.setUsing(false);
        pc_in_use--;
        notifyAll();
    }
    synchronized void labStartUse(){
        while(using || pc_in_use != 0){
            try{
                wait();
            }catch (InterruptedException e){}
        }
        using = true;
    }

    synchronized void labEndUse(){
        using = false;
        notifyAll();
    }
    public synchronized int isFree(){
        for(Computer i : computers){
            if(!i.isUsing() && (i.t_queue.isEmpty())){
                return computers.indexOf(i);
            }
        }
        return -1;
    }

    synchronized Computer studentPcStartUse(){
        int i;
        while(true) {
            i = isFree();
            if (i != -1) {
                //pcStartUse(computers.get(i));
                return computers.get(i);
            }
        }
    }

    synchronized void pcStartUse(Computer pc){
        while(pc.isUsing() || using || pc_in_use == 20 ){
            try{
                wait();
            }catch (InterruptedException e){}
        }
      // System.out.println(pc_in_use);
        pc.setUsing(true);
        pc_in_use++;
    }

    synchronized void pcEndUse(Computer pc){
        pc.setUsing(false);
        pc_in_use--;
        notifyAll();
    }
}
