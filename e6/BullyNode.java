import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;

public class BullyNode extends UnicastRemoteObject implements Node {
    private int nodeId;
    private final int nextNodeId;
    private final AtomicBoolean isParticipating = new AtomicBoolean(false);
    private final AtomicInteger currentLeader = new AtomicInteger(-1);
    private final AtomicBoolean isAlive = new AtomicBoolean(true);
    
    public BullyNode(int nextNodeId) throws RemoteException {
        this.nextNodeId = nextNodeId;
    }
    
    @Override
    public void setNodeId(int id) throws RemoteException {
        this.nodeId = id;
    }
    
    @Override
    public int getNodeId() throws RemoteException {
        return nodeId;
    }
    
    @Override
    public boolean isAlive() throws RemoteException {
        return isAlive.get();
    }
    
    @Override
    public void startElection() throws RemoteException {
        System.out.println("(" + nodeId + ") : Starting election (" + Thread.currentThread().getName() + ")");
        isParticipating.set(true);
        
        // Send election message to the next node if it has a higher ID
        if (nextNodeId > nodeId) {
            try {
                String nextNodeUrl = "rmi://localhost/n" + nextNodeId;
                Node nextNode = (Node) Naming.lookup(nextNodeUrl);
                Message electionMsg = new Message(Message.Type.ELECTION, nodeId, nextNodeId, 0);
                nextNode.receiveMessage(electionMsg);
            } catch (Exception e) {
                System.out.println("(" + nodeId + ") : Next node is not responding (" + Thread.currentThread().getName() + ")");
                becomeCoordinator();
            }
        } else {
            becomeCoordinator();
        }
    }
    
    private void becomeCoordinator() throws RemoteException {
        System.out.println("(" + nodeId + ") : Becoming coordinator (" + Thread.currentThread().getName() + ")");
        currentLeader.set(nodeId);
        isParticipating.set(false);
        
        // Send coordinator message to the next node if it has a lower ID
        if (nextNodeId < nodeId) {
            try {
                String nextNodeUrl = "rmi://localhost/n" + nextNodeId;
                Node nextNode = (Node) Naming.lookup(nextNodeUrl);
                Message coordinatorMsg = new Message(Message.Type.COORDINATOR, nodeId, nextNodeId, 0);
                nextNode.receiveMessage(coordinatorMsg);
            } catch (Exception e) {
                System.out.println("(" + nodeId + ") : Failed to notify next node (" + Thread.currentThread().getName() + ")");
            }
        }
    }
    
    @Override
    public void receiveMessage(Message message) throws RemoteException {
        System.out.println("(" + nodeId + ") : Received " + message + " (" + Thread.currentThread().getName() + ")");
        
        switch (message.getType()) {
            case ELECTION:
                // Send OK message to the sender
                try {
                    String senderUrl = "rmi://localhost/n" + message.getSenderId();
                    Node sender = (Node) Naming.lookup(senderUrl);
                    Message okMsg = new Message(Message.Type.OK, nodeId, message.getSenderId(), 0);
                    sender.receiveMessage(okMsg);
                } catch (Exception e) {
                    System.out.println("(" + nodeId + ") : Failed to send OK message (" + Thread.currentThread().getName() + ")");
                }
                
                // Start a new election if not already participating
                if (!isParticipating.get()) {
                    startElection();
                }
                break;
                
            case OK:
                // Received OK from a higher numbered node, wait for coordinator message
                break;
                
            case COORDINATOR:
                // Update the current leader
                currentLeader.set(message.getSenderId());
                isParticipating.set(false);
                System.out.println("(" + nodeId + ") : New coordinator is node " + message.getSenderId() + " (" + Thread.currentThread().getName() + ")");
                break;
        }
    }
    
    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                System.out.println("Usage: java BullyNode <nodeId> <nextNodeId>");
                return;
            }
            
            int nodeId = Integer.parseInt(args[0]);
            int nextNodeId = Integer.parseInt(args[1]);
            
            BullyNode node = new BullyNode(nextNodeId);
            node.setNodeId(nodeId);
            
            String nodeUrl = "rmi://localhost/n" + nodeId;
            Naming.rebind(nodeUrl, node);
            System.out.println("(" + nodeId + ") : Node registered at " + nodeUrl + " (" + Thread.currentThread().getName() + ")");
            
            // Start election after a delay
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    node.startElection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            
            // Keep the node running
            while (node.isAlive()) {
                Thread.sleep(1000);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 