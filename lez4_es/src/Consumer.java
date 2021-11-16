public class Consumer implements Runnable{
    boolean pd;
    Dropbox dbox;

    public Consumer(boolean pd, Dropbox dbox){
        this.pd = pd;
        this.dbox = dbox;
    }

    public void run(){
        dbox.take(pd);
    }
}
