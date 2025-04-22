import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class RemoteImpl extends UnicastRemoteObject implements RemoteInterface {

    public RemoteImpl() throws RemoteException {
        super();
    }

    @Override
    public int[] sortArray(int[] arr) throws RemoteException {
        System.out.println("Server sorting array: " + Arrays.toString(arr) + " | Thread: "
                + Thread.currentThread().getId());
        int[] sortedArr = arr.clone();
        Arrays.sort(sortedArr);
        return sortedArr;
    }
}
