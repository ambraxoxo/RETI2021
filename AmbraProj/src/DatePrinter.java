import java.util.*;

public class DatePrinter {


    public static void main(String args[]){
        while(true) {
            Calendar data = Calendar.getInstance();
            System.out.printf("%s, %s\n", String.valueOf(data.getTime()), Thread.currentThread().getName());

            try {
                Thread.currentThread().sleep(2000);
            }catch (InterruptedException e){
                return;
            }
        }
    }
}
