import java.io.*;

public class CopyFile {
    public static void main(String[] args) throws IOException {
        OutputStream fileCopy = null;
        InputStream fileIn = null;
        try{
            fileIn = new FileInputStream("./src/file.txt");
            fileCopy = new FileOutputStream("./src/copy.txt");
            int c;
            while((c = fileIn.read()) != -1)
                fileCopy.write(c);

        }finally {
            if(fileIn != null) fileIn.close();
            if(fileCopy != null) fileCopy.close();
        }
    }
}
