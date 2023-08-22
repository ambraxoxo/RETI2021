import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Server {
    private static Properties properties;
    private static Gson gson;
    private static ConcurrentHashMap<String, User> users;
    private static File userDB;
    private static String secretWord;
    private static Set<User> onlineUsers;
    public static RandomAccessFile words;
    public static DatagramSocket multiSocket;
    public static InetAddress group;
    public static int port;
    private static TreeSet<User> leaderboard;
    private static UserRegisterImpl userReg;
    public static ServerSocket socket;


    public static String getSecretWord() {
        return secretWord;
    }

    /**
     * Carica il file server.properties e inizializza il file del vocabolario
     * @throws IOException - se c'è un problema con la lettura dei file o file not found
     */
    public static void loadProperties() throws IOException {
        properties = new Properties();
        properties.load(new FileInputStream("server.properties"));
        words = new RandomAccessFile(properties.getProperty("VOCABULARY"), "r");
    }

    public static long getDelay(){
        return Long.parseLong(properties.getProperty("WORD-LIFESPAN"));
    }

    /**
     * Pubblica l'oggetto remoto su cui il client chiama la registrazione e si iscrive/disiscrive per il callback
     */
    public static void publishObject(){
        int registryPort = Integer.parseInt(properties.getProperty("REGISTRY-PORT"));
        try{
            userReg = new UserRegisterImpl(users);
            LocateRegistry.createRegistry(registryPort);
            Registry r = LocateRegistry.getRegistry(registryPort);
            r.rebind("USER-REGISTRATION", userReg);

        }catch(RemoteException e){
            System.out.println("lol");
        }

    }

    /**
     * Carica i dati dal file userDB.json e li deserializza nella ConcurrentHashMap di utenti registrati,
     * successivamente li copia dalla map al TreeSet della classifica e inizializza il Set degloi utenti online
     */
    public static void loadData() {
        gson = new GsonBuilder().registerTypeAdapter(User.class, new User.Deserializer()).
                setPrettyPrinting().create();
        userDB = new File("userDB.json");
        users = new ConcurrentHashMap<>();
        leaderboard = new TreeSet<>(Comparator.reverseOrder());
        if (userDB.exists()) {
            try {
                FileReader r = new FileReader(userDB);
                Type mapType = new TypeToken<ConcurrentHashMap<String, User>>() {
                }.getType();
                users = gson.fromJson(JsonParser.parseReader(r), mapType);
                for(String key : users.keySet()){
                    leaderboard.add(users.get(key));
                }
                r.close();

            } catch (IOException e) {
            }
        }
        if(users == null) users = new ConcurrentHashMap<>();
        onlineUsers = Collections.synchronizedSet(new HashSet<>());
    }

    public static synchronized boolean userLoggedIn(User u){
        return onlineUsers.add(u);
    }

    /**
     * Metodo invocato dall'oggetto remoto su cui il client esegue la registrazione
     * @param u - utente che deve essere registrato
     * @return - null se l'utente non era registrato, l'oggetto Utente associato alla chiave username se l'utente è già registrato.
     *
     */
    public static synchronized User registerUser(User u){
        User result = users.putIfAbsent(u.getUsername(), u);
        if(result != null) leaderboard.add(u);
        return result;
    }

    public static boolean checkUserLogged(User u){
        return onlineUsers.contains(u);
    }

    /**
     *
     * @return i primi 3 username e punteggio
     */
    private static String getRanks(){
        int count = 0;
        StringBuilder result = new StringBuilder();
        Iterator<User> i = leaderboard.iterator();
        while(i.hasNext() && count < 3){
            User u = i.next();
            result.append(count + 1).append(" - ").append(u.getUsername()).append(": ").append(u.getPunteggio()).append("\n");
            count++;
        }
        System.out.println(result);
        return result.toString();
    }

    /**
     *
     * @param u utente che ha finito di giocare, di cui si deve controllare la posizione in classifica
     * @throws RemoteException se non si riesce a invocare il metodo sull'oggetto remoto
     */
    public static synchronized void checkRanking(User u) throws RemoteException {
        NavigableSet<User> subSet = leaderboard.headSet(u, false);
        if(subSet.size() < 3){
            userReg.update(getRanks());
        }
    }

    /**
     * Serializza la ConcurrentHashMap
     */
    public static synchronized void saveData(){
        try {
            userDB = new File("userDB.json");
            FileWriter writeMap = new FileWriter(userDB);
            String jsonString = gson.toJson(users);
            writeMap.write(jsonString);
            writeMap.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized boolean removeLoggedUser(User u){return onlineUsers.remove(u);}

    public static ServerSocket openSocket() throws IOException {
        int port = Integer.parseInt(properties.getProperty("SERVER-PORT"));
        socket = new ServerSocket(port);
        return socket;
    }

    public static User getUser(String username){
        return users.get(username);
    }

    public static void setSecretWord(String word){
        secretWord = word;
    }

    /**
     * Controlla se la parola word è contenuta nel dizionario
     * @param word parola da cercare
     * @return true se la parola appartiene al dizionario, false altrimenti
     * @throws IOException problemi di accesso al file
     */
    public static boolean findWord(String word) throws IOException {
        long start = 0, end = words.length()/11, mid = end/2;
        boolean result;
        result = binarySearch(start, mid, end, word);
        return result;
    }

    /**
     * Ricerca binaria della parola goal nel file
     */
    public static synchronized Boolean binarySearch(long start, long mid, long end, String goal) throws IOException {
        if(end < start) return false;
        words.seek(mid*11);
        String seekedWord = words.readLine().trim();
        int comparison = seekedWord.compareTo(goal);
        if(comparison == 0) return true;
        if(comparison > 0){
            end = mid - 1;
        }
        else{
            start = mid + 1;
        }
        mid = (start + end)/2;
        return binarySearch(start, mid, end, goal);
    }

    /**
     *
     * @param word parola inviata dall'utente
     * @param secretWord parola che l'utente deve indovinare per questa sessione
     * @return array di 10 elementi che corrispondono alle lettere della parola che l'utente ha inserito:
     *          1 codifica se la lettera è presente nella posizione giusta
     *          2 codifica se la lettera è presente nella parola
     *          0 codifica se la lettera non è nella parola
     */
    public static int[] checkWord(String word, String secretWord){
        char[] currWordArray = secretWord.toCharArray();
        char[] guessedWordArray = word.toCharArray();
        int[] result = new int[10];

        for (int i = 0; i < guessedWordArray.length; i++) {
            if(currWordArray[i] == guessedWordArray[i]) result[i] = 1;
            else if (secretWord.contains(Character.toString(guessedWordArray[i]))){
                result[i] = 2;
            }else
                result[i] = 0;
        }
        return result;
    }

    /**
     * Inizializzazione del gruppo multicast
     */
    public static void initMulticastSocket() throws SocketException, UnknownHostException {
        port = Integer.parseInt(properties.getProperty("MULTICAST-PORT"));
        multiSocket = new DatagramSocket();
        InetAddress groupAddress = InetAddress.getByName(properties.getProperty("MULTICAST-IP"));
        if(!groupAddress.isMulticastAddress()){
            throw new IllegalArgumentException(groupAddress + "is not a multicast address");
        }
        group = InetAddress.getByName(properties.getProperty("MULTICAST-IP"));

    }

    /**
     * Condivisione del risultato al gruppo multicast
     * @param u utente che ha chiesto di condividere il suo risultato
     * @param result risultato dell'utente
     */
    public static void share(User u, String result) throws IOException {
        String sharable = u.getUsername() + "\n" + result;
        byte[] buf;
        buf = sharable.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, port);
        multiSocket.send(packet);
    }

    /**
     * Chiusura delle connessioni
     */
    public static void closeConnections() throws IOException {
        multiSocket.close();
        UnicastRemoteObject.unexportObject(userReg, true);
        socket.close();
    }



}
