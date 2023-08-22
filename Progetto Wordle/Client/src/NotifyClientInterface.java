import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NotifyClientInterface extends Remote {
    void notifyClient(String leaderboard) throws RemoteException;
}
