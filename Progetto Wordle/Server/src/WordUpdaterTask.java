import java.io.*;
import java.util.Properties;

public class WordUpdaterTask implements Runnable{
    private static RandomAccessFile words;
    private static final int wordLength = 11; // 10 caratteri + \n
    private static long nWords;
    Properties properties;

    public WordUpdaterTask() throws IOException {
        properties = new Properties();
        properties.load(new FileInputStream("server.properties"));
        File vocab = new File(properties.getProperty("VOCABULARY"));
        words = new RandomAccessFile(vocab.getPath(), "r");
        nWords = words.length()/ wordLength;
    }

    /**
     * Effettua l'update della parola da indovinare tramite un RandomAccessFile generando casualmente un punto dove accedere al file
     */
    public static void updateWord() throws IOException {
        long seed = (long) (Math.random() * nWords);
        words.seek(seed* wordLength);
        String newWord = words.readLine().trim();
        System.out.println("[Word updated to: " + newWord + "]");
        Server.setSecretWord(newWord);
    }

    public void run(){
        try {
            updateWord();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
