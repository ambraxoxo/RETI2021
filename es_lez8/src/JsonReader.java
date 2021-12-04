import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonReader {
    private final static int nThreads = 20;
    static volatile int[] occorrenzePagamenti = new int[5]; //array che conta le causali dei pagamenti

    public static void main(String[] args) {
        try{
            letturaFile();
        }catch(IOException e){
            System.out.println("Errore lettura file 17 JsonReader");
        }
    }
}
