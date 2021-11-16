public class Main {

    public static void main(String args[]){
        //estraggo la dimenione dagli argomenti
        int dim = Integer.parseInt(args[0]);
        //creo nuovo oggetto ufficio
        Ufficio ufficio = new Ufficio(dim);
        //eseguo ufficio

        ufficio.esegui();
    }
}
