import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote {

    int[] sortArray(int[] arr) throws RemoteException;
}
