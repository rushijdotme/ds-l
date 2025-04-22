import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

public class TokenRingNodeImpl extends UnicastRemoteObject implements
        TokenRingNode {

    private final String nextNodeName;
    private final String nodeName;
    private final AtomicBoolean hasToken = new AtomicBoolean(false);
    private final AtomicBoolean wantsCriticalSection = new AtomicBoolean(false);
    private TokenRingNode cachedNextNode = null;
    private static final AtomicInteger sharedCounter = new AtomicInteger(0);
    private static final Random random = new Random();

    public TokenRingNodeImpl(String nodeName, String nextNodeName) throws RemoteException {
        this.nodeName = nodeName;
        this.nextNodeName = nextNodeName;
    }

    @Override
    public void receiveToken() throws RemoteException {
        new Thread(() -> {
            hasToken.set(true);
            System.out.println("(" + nodeName + ") : received token (" + Thread.currentThread().getName() + ")");

            if (wantsCriticalSection.get()) {
                enterCriticalSection();
                wantsCriticalSection.set(false);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            passToken();
        }).start();
    }

    private void enterCriticalSection() {
        System.out.println("(" + nodeName + ") : ENTERING CRITICAL SECTION (" + Thread.currentThread().getName() + ")");
        try {
            // Increment counter by a random number between 1 and 10
            int increment = random.nextInt(10) + 1;
            int newValue = sharedCounter.addAndGet(increment);
            System.out.println("(" + nodeName + ") : incremented counter by " + increment + ". New value: " + newValue + " (" + Thread.currentThread().getName() + ")");
            
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("(" + nodeName + ") : EXITING CRITICAL SECTION (" + Thread.currentThread().getName() + ")");
    }

    private void passToken() {
        try {
            Thread.sleep(8000);
            if (cachedNextNode == null) {
                cachedNextNode = (TokenRingNode) Naming.lookup(nextNodeName);
            }

            System.out.println("(" + nodeName + ") : passing token to " + nextNodeName + " (" + Thread.currentThread().getName() + ")");
            hasToken.set(false);
            cachedNextNode.receiveToken();
        } catch (Exception e) {
            System.out.println("(" + nodeName + ") : Ring broken: " + e.getMessage() + " (" + Thread.currentThread().getName() + ")");
            running = false;
        }
    }

    public void requestCriticalSection() {
        wantsCriticalSection.set(true);
    }

    private static volatile boolean running = true;

    public static void stop() {
        running = false;
    }

    public static void main(String[] args) {
        try {
            String nodeName = args[0];
            String nextNodeName = args[1];
            TokenRingNodeImpl node = new TokenRingNodeImpl(nodeName, nextNodeName);

            Naming.rebind(nodeName, node);
            System.out.println("(" + nodeName + ") : registered. Next node: " + nextNodeName + " (" + Thread.currentThread().getName() + ")");

            if (args.length > 2 && args[2].equals("init")) {
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        node.receiveToken();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            // Continuously request critical section access
            new Thread(() -> {
                while (running) {
                    try {
                        Thread.sleep(5000);
                        node.requestCriticalSection();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            while (running) {
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
