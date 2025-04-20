
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class WorkerImpl extends UnicastRemoteObject implements Worker {

    public WorkerImpl() throws RemoteException {
        super();
    }

    @Override
    public int calculateSum(int[] subArray) throws RemoteException {
        int sum = 0;
        for (int num : subArray) {
            sum += num;
        }
        System.out.println("Executing sum on: " + subArray.toString());
        return sum;
    }
}
