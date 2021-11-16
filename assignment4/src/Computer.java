import java.util.ArrayList;
import java.util.LinkedList;

public class Computer {
    private boolean using;
    public LinkedList<Object> t_queue = new LinkedList<>();

    public Computer(){
        this.using = false;
    }

    public synchronized boolean isUsing() {
        return using;
    }

    public synchronized void setUsing(boolean using) {
        this.using = using;
    }

    public synchronized void addTes(){
        t_queue.add(new Object());
    }
    public synchronized void decTes(){
        t_queue.remove();
    }
}
