public class Producer implements Runnable{
    Dropbox dbox;

    public Producer(Dropbox dbox){
        this.dbox = dbox;
    }

    public void run(){
        int n = (int)(Math.random()*100);
        dbox.put(n);
    }
}
