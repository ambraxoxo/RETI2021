public class MainClass {

    public static void main(String[] args){
        Dropbox dbox = new Dropbox();
        Producer prod = new Producer(dbox);
        Consumer c1 = new Consumer(true, dbox);
        Consumer c2 = new Consumer(false, dbox);
        do {
            prod.run();
            c1.run();
            c2.run();
        }while(true);

    }
}
