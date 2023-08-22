import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;

public class NotifyClientImpl extends RemoteObject implements NotifyClientInterface{

    public NotifyClientImpl() throws RemoteException{
        super();
    }


    @Override
    public void notifyClient(String leaderboard) throws RemoteException {
        System.out.println("La classifica è stata aggiornata: \n" + leaderboard);

    }
}
