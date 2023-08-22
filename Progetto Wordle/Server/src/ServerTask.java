import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

public class ServerTask implements Runnable{
    private final BufferedWriter writer;
    private final BufferedReader reader;
    private User user;
    private int nTries;
    private boolean won;
    private final Socket connection;
    private String playingWord;
    private String userMatch;
    StringBuilder matchResult;
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String RESET = "\033[0m";


    public ServerTask(Socket connection) throws IOException {
        this.connection = connection;
        this.writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        this.reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        this.user = null;
        this.nTries = 0;
        this.won = false;
    }

    /**
     * Fa la richiesta HTTP all'API del servizio di traduzione e parsa il json ricevuto
     * @return la traduzione della parola che è stata giocata
     */
    private String getTranslation() throws IOException {
        URL url = new URL("https://api.mymemory.translated.net/get?q=" + playingWord + "&langpair=en|it");
        String response, translation = "";
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))){
            response = reader.readLine();
        }
        try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
            translation = jsonObject
                    .getAsJsonObject("responseData")
                    .get("translatedText")
                    .getAsString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ("Traduzione: " + translation + "\n");
    }

    /**
     * Legge da stream username e password, controlla se l'utente è registrato e se è già loggato
     * scrive al client il risultato dell'operazione
     */
    public void login(){
        String username;
        String password;
        User userLogin;
        try {
            do {
                username = this.reader.readLine().trim();
                password = reader.readLine().trim();
                if((userLogin = Server.getUser(username)) != null) {
                    if (userLogin.getPassword().equals(password)) {
                        if(!Server.userLoggedIn(userLogin)) {
                            writer.write(2); //utente già loggato
                            writer.flush();
                            return;
                        }
                        writer.write(1); //il login ha avuto successo
                        writer.flush();
                        user = Server.getUser(username);
                    }
                    return;
                }else {
                    //dati login errati
                    writer.write(3);
                    writer.flush();
                }
            }while(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Inizializza la nuova partita del client, controllando prima se può giocare, ovvero se l'ultima parola che ha giocato è diversa da quella della sessione corrente
     * @throws IOException
     */
    public void playWordle() throws IOException {
        if(user == null)
            return;
        if(Objects.equals(user.getLastWordPlayed(), Server.getSecretWord())){
            writer.write(0); //l'utente ha già giocato per questa parola
            writer.flush();
            return;
        }
        nTries = 0;
        user.setPlayedMatches(user.getPlayedMatches() + 1);
        Server.saveData();
        writer.write(1); //l'utente può giocare
        writer.flush();
        playingWord = Server.getSecretWord();

    }

    /**
     * Aggiorna i dati dell'utente con l'ultimo match giocato
     * @throws IOException
     */
    private void matchEnd() throws IOException {
        if(nTries == 12) nTries--;
        user.setPunteggio(user.getPunteggio()+(12 - nTries)); //aggiorna punteggio
        userMatch = matchResult.toString(); //crea la stringa del risultato coi quadratini
        writer.write(matchResult.toString());
        writer.write("La parola era " + playingWord + "\n");
        writer.write(getTranslation());
        writer.write("done\n");
        Server.checkRanking(user);
        Server.saveData();
        writer.flush();
    }

    /**
     * Effettua il logout dell'utente
     * @throws IOException
     */
    public void logout() throws IOException {
        if(!Server.checkUserLogged(user)){
            System.out.println("l'utente " + user.getUsername() + "non è loggato");
            return;
        }
        if(Server.removeLoggedUser(user)){
            System.out.println("l'utente "+ user.getUsername() + " è stato rimosso correttamente");
            connection.close();
        }
    }

    /**
     *
     * @param i numero di tentativi che ci ha messo l'utente per indovinare la parola
     */
    public void setUserGD(int i){
        int[] gd = user.getGuessDistribution();
        gd[i] ++;
        user.setGuessDistribution(gd);
        Server.saveData();
    }

    public void buildMatchResult(int[] guessArray){
        System.out.println(Arrays.toString(guessArray));
        for (int j : guessArray) {
            switch (j) {
                case 0: {
                    matchResult.append(RESET).append("\u25A0").append(RESET);
                    break;
                }
                case 1: {
                    matchResult.append(ANSI_GREEN).append("\u25A0").append(RESET);
                    break;
                }
                case 2: {
                    matchResult.append(ANSI_YELLOW).append("\u25A0").append(RESET);
                    break;
                }
            }
        }

    }

    /**
     * Effettiva sessione di gioco dell'utente, riceve un tentativo dal client, controlla se ha vinto.
     * Se non ha vinto manda il risultato del tentativo e aspetta il tentativo successivo.
     * A fine match aggiorna le statistiche dell'utente e controlla se ci sono stati cambiamenti nella classifica
     * @throws IOException
     */
    public void sendWord() throws IOException {
        int[] result;
         matchResult = new StringBuilder();
        if(Objects.equals(user.getLastWordPlayed(), Server.getSecretWord())){
            writer.write(0);
            writer.flush();
            return;
        }else{
            user.setLastWordPlayed(Server.getSecretWord());
            writer.write(1);
            writer.flush();
        }
        while(nTries < 12) {
            String guessedWord = reader.readLine().trim();
            if(Server.findWord(guessedWord)) {
                if (guessedWord.equals(playingWord)) {
                    writer.write(1);
                    user.setWonMatches(user.getWonMatches() + 1);
                    matchResult.append(ANSI_GREEN).append("\u25A0\u25A0\u25A0\u25A0\u25A0\u25A0\u25A0\u25A0\u25A0\u25A0").append(RESET).append("\n");
                    int streak = user.getStreak();
                    user.setStreak(streak + 1);
                    if(user.getLongestStreak() < user.getStreak()) user.setLongestStreak(streak);
                    setUserGD(nTries);
                    matchEnd();

                    return;
                } else {
                    result = Server.checkWord(guessedWord, playingWord);
                    writer.write(2);
                    writer.write(Arrays.toString(result) + '\n');
                    buildMatchResult(result);
                    writer.flush();
                    nTries++;
                }
            }else{
                writer.write(3);
                writer.flush();
            }
            matchResult.append("\n");
        }
        matchEnd();

    }

    /**
     * Invia le statistiche dell'utente al client che le ha richieste
     */
    public void sendStatistics() throws IOException{
        double winPerc = 0;
        int wonM = user.getWonMatches(), pM = user.getPlayedMatches();
        if(pM != 0){
            winPerc = ((double) wonM/(double) pM)*100;
            user.setWinPerc((int)winPerc);
        }

        writer.write(user.getPlayedMatches());
        writer.write((int) winPerc);
        writer.write(user.getStreak());
        writer.write(user.getLongestStreak());
        writer.write(Arrays.toString(user.getGuessDistribution()) + "\n");
        writer.flush();

    }

    /**
     * Condivide sul gruppo multicast il risultato del match dell'utente che ha richiesto la condivisione
     */
    private void share() throws IOException {
        if(userMatch.isEmpty()){
            writer.write(0); //Non ha giocato
            writer.flush();
            return;
        }
        Server.share(user, userMatch);
        writer.write(1); //Condivisione effettuata
        writer.flush();
    }

    @Override
    public void run() {
        while (true) {
            try {
                switch (reader.read()) {
                    case 1:
                        login();
                        System.out.println("[User logged in: " + user.getUsername() +"]");
                        break;
                    case 2:
                        System.out.println("[User " + user.getUsername() + " is starting the game]");
                        playWordle();
                        break;
                    case 3:
                        System.out.println("[User " + user.getUsername() + " is starting a new match]");
                        sendWord();
                        break;
                    case 4:
                        System.out.println("[User " + user.getUsername() + " requested statistics]");
                        sendStatistics();
                        break;
                    case 5:
                        System.out.println("[User " + user.getUsername() + " requested share]");
                        share();
                        break;
                    case 0:
                        System.out.println("[User " + user.getUsername() + " requested logout]");
                        logout();
                        break;

                }
            } catch (IOException e) {
            }
        }
    }

}
