

import java.util.InputMismatchException;

public class MainClass {
    public static void main(String args[]){
    int studenti = 0 , prof = 0 , tesisti = 0;

   try{
        studenti = Integer.parseInt(args[0]);
        prof = Integer.parseInt(args[1]);
        tesisti = Integer.parseInt(args[2]);
    }catch(InputMismatchException e){
        System.out.println("Errore negli argomenti");
    }
    Tutor tutor = new Tutor(studenti, prof, tesisti);
    tutor.esegui();

    }

}
