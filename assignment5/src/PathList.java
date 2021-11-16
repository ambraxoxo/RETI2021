import java.util.LinkedList;

public class PathList {
    public LinkedList<String> lista = new LinkedList<>();
    public boolean using;

    public PathList() {
        this.using = false;
    }

    public synchronized void put(String path) {
        while(using) {
            try {
                this.wait();
            } catch (InterruptedException e) {}
        }
        using = true;
          lista.push(path);

        using = false;
        this.notify();
    }

    public synchronized String get(){
        String result;
        while(using){
            try {
                this.wait();
            }catch(InterruptedException e){}
        }
        using = true;
        if(!lista.isEmpty()){
            result = lista.remove();
        }else {
            result = "Lista vuota";
        }
        using = false;
        this.notifyAll();
        return result;
    }
}
