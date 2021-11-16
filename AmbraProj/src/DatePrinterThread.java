import java.util.*;

public class DatePrinterThread extends Thread{

    public void run(){
        while (true) {
            Calendar data = Calendar.getInstance();
            System.out.printf("%s, %s\n", String.valueOf(data.getTime()), Thread.currentThread().getName());

            try {
                Thread.currentThread().sleep(2000);
            }catch (InterruptedException e){
                return;
            }

        }
    }
    public static void main(String args[]){
        DatePrinterThread date = new DatePrinterThread();
        date.start();
    }
}
