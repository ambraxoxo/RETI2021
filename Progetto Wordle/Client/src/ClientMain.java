import java.io.*;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.Scanner;

public class ClientMain {
    private static Scanner scanner;
    private static BufferedReader reader;
    private static BufferedWriter writer;
    private static Properties properties;
    private static boolean isLogged = false;
    private static boolean isPlaying = false;
    private static boolean gameEnded = false;
    private static Socket connection;
    private static StringBuilder buildRoundResult;
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String RESET = "\033[0m";
    public static NotificationThread notif = null;
    private static String username;
    private static UserRegister user;
    private static NotifyClientInterface callbackObj;

    private static void registerForCallback() throws RemoteException {
        callbackObj = new NotifyClientImpl();
        NotifyClientInterface stub = (NotifyClientInterface) UnicastRemoteObject.exportObject(callbackObj, 0);
        (user).registerForCallback(stub);
    }

    private static void initRMI(){
        int registryPort;
        registryPort = Integer.parseInt(properties.getProperty("REGISTRY-PORT"));
        try {
            Registry registry = LocateRegistry.getRegistry(registryPort);
            Remote serverRMI = registry.lookup("USER-REGISTRATION");
            user = (UserRegister) serverRMI;
        }catch (RemoteException | NotBoundException e){
            System.out.println("Errore nell'RMI");
        }

    }
    private static void register() {
        String password;
        scanner = new Scanner(System.in);

        System.out.println("Inserisci username");
        username = scanner.next();
        System.out.println("Inserisci password");
        password = scanner.next();
        try {

            switch (user.register(username, password)) {
                case 0:
                    System.out.println("Username in uso");
                    return;
                case 1:
                    System.out.println("Registrazione completata");
                default:
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static String getUsername(){ return username;}

    private static void loadProperties() throws IOException {
        properties = new Properties();
        properties.load(new FileInputStream("client.properties"));

    }

    private static void joinMulticast() throws IOException, InterruptedException {
        notif = new NotificationThread(properties.getProperty("MULTICAST-IP"), Integer.parseInt(properties.getProperty("MULTICAST-PORT")));
        notif.start();
    }

    /**
     * Si connette al server chiede all'utente username e password e stampa il risultato dell'operazione
     */
    private static void login() {
        String password;

        try {
            connection = new Socket(properties.getProperty("SERVER-IP"), Integer.parseInt(properties.getProperty("SERVER-PORT")));
            writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            System.out.println("errore socket");
        }


        try {
            //mando al server un codice che indica che richiedo l'operazione di login
            boolean loop = true;
            writer.write(1);
            while (loop) {
                System.out.println("Inserisci username: ");
                username = scanner.next();
                writer.write(username + "\r\n");
                System.out.println("Inserisci password");
                password = scanner.next();
                writer.write(password + "\r\n");
                writer.flush();
                int a = reader.read();
                switch (a) {
                    case 1:
                        System.out.println("Il login ha avuto successo");
                        loop = false;
                        isLogged = true;
                        joinMulticast();
                        registerForCallback();
                        break;
                    case 2:
                        System.out.println("Utente già loggato");
                        loop = false;
                        break;
                    case 3:
                        System.out.println("Dati login errati");
                        loop = false;
                        break;
                    default:
                        break;
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Invia al client la richiesta di iniziare una nuova partita
     */
    private static void playWordle() throws IOException {
        writer.write(2);
        writer.flush();
        if (reader.read() == 1) {
            System.out.println("Partita iniziata");
            isPlaying = true;
        } else
            System.out.println("Hai già giocato per questa parola");

    }

    /**
     * Parsa il risultato ottenuto dal server: stringa che rappresenta un array di int che codifica il risultato del tentativo dell'utente
     * @param result stringa che rappresenta l'array di int
     * @param guess tentativo dell'utente
     */
    private static void buildResult(String result, String guess) {
        String[] guessArray = guess.split("");
        buildRoundResult = new StringBuilder();
        String[] a = result.replace("[", "").replace("]", "").split(",");
        for (int i = 0; i < a.length; i++) {
            switch (a[i].trim()) {
                case "0": {
                    buildRoundResult.append(guessArray[i]);
                    break;
                }
                case "1": {
                    buildRoundResult.append(ANSI_GREEN).append(guessArray[i]).append(RESET);
                    break;
                }
                case "2": {
                    buildRoundResult.append(ANSI_YELLOW).append(guessArray[i]).append(RESET);
                    break;

                }

            }
        }
    }

    /**
     * Legge da tastiera i tentativi dell'utente, li manda al server, legge la risposta e l parsa chiamando buildResult, poi stampa il risultato
     */
    private static void sendWord() throws IOException {
        scanner.reset();
        writer.write(3);
        writer.flush();
        String line;
        String guess;
        int nTries = 1;
        if(reader.read() == 0){
            System.out.println("Hai già giocato per questa parola");
        }else {
            while (nTries < 13) {
                do {
                    System.out.print("Tentativo " + nTries + ": ");
                    guess = scanner.next().trim();
                    if (guess.length() != 10) System.out.println("La parola deve essere di 10 lettere");
                } while (guess.length() != 10);
                writer.write(guess + '\n');
                writer.flush();
                switch (reader.read()) {
                    case 1:
                        System.out.println("Hai vinto!");
                        gameEnded = true;
                        isPlaying = false;
                        while(!(line = reader.readLine()).equals("done"))
                            System.out.println(line);
                        return;
                    case 2:
                        String result = reader.readLine();
                        buildResult(result, guess);
                        System.out.println(buildRoundResult.toString());
                        nTries++;
                        break;
                    case 3:
                        System.out.println("La parola non appartiene al dizionario");
                        break;
                }
            }
            if(nTries == 13){
                System.out.println("Non hai indovinato la parola");
                while(!(line = reader.readLine()).equals("done"))
                    System.out.println(line);
            }
            System.out.println();

        }

    }

    /**
     * Parsa la stringa della guess distribution e la stampa
     * @param gd stringa he rappresenta l'array della guess distribution
     */
    private static void printGuessDistribution(String gd){
        String[] elements = gd.replace("[", "").replace("]", "").split(",");
        for (int i = 0; i < elements.length; i++) {
            System.out.print(i + 1 + ": " );
            System.out.println(new String(new char[Integer.parseInt(String.valueOf(elements[i]).trim())]).replace('\0', '\u25A0'));
        }
    }

    /**
     * Richiede al server le statistiche dell'utente e le stampa
     */
    private static void showMeStatistics() throws IOException{
        writer.write(4);
        writer.flush();
        int playedMatches = reader.read();
        int winPerc = reader.read();
        int streak = reader.read();
        int longest = reader.read();
        String gd = reader.readLine();
        System.out.println("Partite giocate: " + playedMatches);
        System.out.println("Percentuale di vittorie: " + winPerc + "%");
        System.out.println("Streak di vittorie: " + streak);
        System.out.println("Streak di vittorie più lunga: " + longest);
        printGuessDistribution(gd);

    }

    /**
     * Richiede al server di condividere il risultato al gruppo multicast
     */
    private static void share() throws IOException {
        writer.write(5);
        writer.flush();
        if(reader.read() == 0) System.out.println("Non hai niente da condividere");
        else System.out.println("Risultato condiviso con successo");
    }

    /**
     * Richiede al server il logout e chiude le connessioni
     */
    private static void logout() throws IOException {
        writer.write(0);
        writer.flush();
        reader.close();
        writer.close();
        connection.close();
        System.out.println("Logout effettuato con successo");
        isPlaying = false;
        isLogged = false;
        notif.interrupt();
        notif = null;
        user.unregisterFromCallback(callbackObj);

    }

    /**
     * Invoca il metodo printNotifications del thread NotificationThread notif che stampa le le condivisioni al gruppo multicast degli altri client
     */
    private static void showMeSharing(){
        notif.printNotifications();
    }

    public static void main(String[] args) throws IOException {
        boolean loop = true;
        scanner = new Scanner(System.in);
        loadProperties();
        initRMI();

        while (loop) {
            if (!isLogged) {
                System.out.println("[1] Registrati");
                System.out.println("[2] Log In");
                System.out.println("[0] Esci");

                while (!scanner.hasNextInt()) {
                    System.out.println("Input non riconosciuto");
                    scanner.nextLine();
                }
                switch (scanner.nextInt()) {
                    case 1:
                        register();
                        break;

                    case 2:
                        login();
                        break;

                    case 0:
                        loop = false;
                        System.exit(0);

                }
            } else {
                if (!isPlaying)
                    System.out.println("[1] Play Wordle");
                else
                    System.out.println("[2] Send Word");
                System.out.println("[3] Show me statistics");
                System.out.println("[4] Show me Sharing");
                if(gameEnded) System.out.println("[5] Share results");
                System.out.println("[0] Logout");

                    while (!scanner.hasNextInt()) {
                        System.out.println("Input non riconosciuto");
                        scanner.nextLine();
                    }
                    switch ((scanner.nextInt())) {
                        case 1:
                            playWordle();
                            break;
                        case 2:
                            sendWord();
                            break;
                        case 3:
                            showMeStatistics();
                            break;
                        case 4:
                            showMeSharing();
                            break;
                        case 5:
                            share();
                            break;
                        case 0:
                            logout();
                            break;
                    }
                }

            }
        }

    }

