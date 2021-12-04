import java.util.Random;
import java.util.UUID;

public class Test {
    public static void main(String[] args) {
        Random ran = new Random();
        int id = ran.next(32);
        System.out.println(id);
    }
}
