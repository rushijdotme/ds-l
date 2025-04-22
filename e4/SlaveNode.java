import java.io.*;
import java.net.*;

public class SlaveNode {
    private static final String MASTER_HOST = "localhost";
    private static final int MASTER_PORT = 5000;
    private long localTime;
    private String name;
    private Socket masterSocket;
    private boolean isRunning = true;

    public SlaveNode(String name, long initialTime) {
        this.name = name;
        this.localTime = initialTime;
    }

    public void start() {
        try {
            // Try to connect to master
            masterSocket = new Socket(MASTER_HOST, MASTER_PORT);
            System.out.println(name + " connected to master with initial time " + localTime);

            // Start thread to handle messages from master
            new Thread(() -> {
                try {
                    while (isRunning) {
                        ObjectInputStream in = new ObjectInputStream(masterSocket.getInputStream());
                        ClockMessage message = (ClockMessage) in.readObject();
                        
                        switch (message.getType()) {
                            case GET_TIME:
                                // Send time response
                                ObjectOutputStream out = new ObjectOutputStream(masterSocket.getOutputStream());
                                out.writeObject(new ClockMessage(ClockMessage.Type.TIME_RESPONSE, localTime, name));
                                break;
                                
                            case ADJUST_TIME:
                                // Adjust local time
                                localTime += message.getTime();
                                System.out.println(name + " adjusted by " + message.getTime() + ". New time: " + localTime);
                                break;
                        }
                    }
                } catch (Exception e) {
                    if (isRunning) {
                        System.err.println("Error in message handling: " + e.getMessage());
                    }
                }
            }).start();

        } catch (IOException e) {
            System.err.println("Failed to connect to master: " + e.getMessage());
            System.exit(1);
        }
    }

    public void stop() {
        isRunning = false;
        try {
            if (masterSocket != null) {
                masterSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java SlaveNode <name> <initial_time>");
            System.exit(1);
        }

        String name = args[0];
        long initialTime = Long.parseLong(args[1]);
        
        SlaveNode slave = new SlaveNode(name, initialTime);
        slave.start();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(slave::stop));
    }
}
