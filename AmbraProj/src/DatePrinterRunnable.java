import java.util.*;

public class DatePrinterRunnable implements Runnable {

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
        DatePrinterRunnable date = new DatePrinterRunnable();
        Thread thread = new Thread(date);
        thread.start();
        try{
            Thread.currentThread().sleep(6000);
        }catch(InterruptedException x){
            return;
        }
        thread.interrupt();
        System.out.printf("%s\n", Thread.currentThread().getName());
    }
}
