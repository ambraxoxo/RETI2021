import java.io.*;
import java.util.LinkedList;

public class Producer implements Runnable{
    private File path;
    private PathList lista;

    public Producer(File path, PathList lista) {
        this.path = path;
        this.lista = lista;

    }

    public void writeList(File[] directories){
        if(directories == null){
            return;
        }
        for(File curr_d : directories){
            if(curr_d.isDirectory()){
                lista.put(curr_d.getPath());
                File[] new_dir = curr_d.listFiles();
                writeList(new_dir);
            }
        }
    }
    @Override
    public void run() {
        LinkedList<File> directories = new LinkedList<>();
        directories.add(path);

            if(directories.size() > 0){
                             writeList(directories.poll().listFiles());
            }

     //   System.out.println(lista.lista.toString());
    }
}
