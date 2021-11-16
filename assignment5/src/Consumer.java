import java.io.*;


public class Consumer implements Runnable{
    private PathList p_list;

    public Consumer(PathList p_list) {
        this.p_list = p_list;
    }

    public void run(){
        String path;
        path = p_list.get();
        File file = new File(path);
        File[] fileList = file.listFiles();
        if(fileList != null) {
            for (File curr_f : fileList) {
                if (curr_f.isFile()) {
                    System.out.println(curr_f.getPath());
                }
            }
        }

    }
}
