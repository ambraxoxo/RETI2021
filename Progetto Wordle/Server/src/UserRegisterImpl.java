
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UserRegisterImpl extends UnicastRemoteObject implements UserRegister {
    ConcurrentHashMap<String, User> users;
    User u;
    private final List<NotifyClientInterface> clients;

    public UserRegisterImpl(ConcurrentHashMap<String, User> users) throws RemoteException{
        this.users = users;
        clients = new ArrayList<>();


    }

    /**
     *
     * @param username username utente
     * @param password  password
     * @return risultato operazione 1: successful 0: username in uso
     * @throws RemoteException se c'è qualche problema col servizio remoto
     */
    @Override
    public int register(String username, String password) throws RemoteException {
        u = new User(username, password);

        if(Server.registerUser(u) == null) {
            Server.saveData();
            return 1;
        }
        return 0;

    }

    /**
     * Inizia i callback della classifica
     * @param ranks prime 3 posizioni della classifica
     */
    public synchronized void update(String ranks) throws RemoteException {
        doCallbacks(ranks);
    }

    /**
     * Notifica i client registrati al callback che le prime 3 posizioni sono cambiate
     * @param ranks prime 3 posizioni della classifica
     */
    private synchronized void doCallbacks(String ranks) throws RemoteException{
        System.out.println("[Starting callbacks]");
        for (NotifyClientInterface client : clients) {
            try {
                client.notifyClient(ranks);
            }catch(RemoteException e){
                e.printStackTrace();
            }
        }
    }

    public synchronized void registerForCallback(NotifyClientInterface clientInterface)throws RemoteException{
        if(!clients.contains(clientInterface)) {
            clients.add(clientInterface);
            System.out.println("[Nuovo client registrato per il callback]");
        }
    }

    @Override
    public synchronized void unregisterFromCallback(NotifyClientInterface clientInterface) throws RemoteException {
        if(clients.remove(clientInterface)) System.out.println("[Client rimosso dal callback]");
        else System.out.println("[Impossibile rimuovere client]");
    }

}

