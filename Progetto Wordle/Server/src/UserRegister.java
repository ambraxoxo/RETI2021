import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UserRegister extends Remote {
    int register(String username, String password) throws RemoteException;
    public void registerForCallback(NotifyClientInterface clientInterface)throws RemoteException;
    public void unregisterFromCallback(NotifyClientInterface clientInterface) throws RemoteException;


}