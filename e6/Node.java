import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node extends Remote {
    void receiveMessage(Message message) throws RemoteException;
    void startElection() throws RemoteException;
    void setNodeId(int id) throws RemoteException;
    int getNodeId() throws RemoteException;
    boolean isAlive() throws RemoteException;
} 