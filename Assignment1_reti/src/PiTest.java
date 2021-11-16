import java.util.*;

public class PiTest {
    public static void main(String args[]){
        float pi = 4, div = 3;
        int count = 1;
        while(true){
            if((count % 2) == 0){
                pi = pi + 4/div;
             //   System.out.println("- 4/" + div);
            }else{
                pi = pi - 4/div;
              //  System.out.println("+ 4/" + div);

            }
            System.out.println(Math.abs(pi - Math.PI) );
            System.out.println(pi);

            count++;
            div += 2;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
