import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class MasterNode {
    private static final int PORT = 5000;
    private static long masterTime;
    private static ServerSocket serverSocket;
    private static Set<Socket> slaveConnections = ConcurrentHashMap.newKeySet();
    private static boolean isRunning = true;

    public static void main(String[] args) {
        try {
            // Set master's initial time (default: 0 for testing)
            masterTime = (args.length > 0) ? Long.parseLong(args[0]) : 0;
            
            // Start server socket
            serverSocket = new ServerSocket(PORT);
            System.out.println("Master node started on port " + PORT);
            
            // Start thread to accept new connections
            new Thread(() -> {
                while (isRunning) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        slaveConnections.add(clientSocket);
                        System.out.println("New slave connected: " + clientSocket.getInetAddress());
                        // Trigger synchronization when new slave joins
                        synchronizeClocks();
                    } catch (IOException e) {
                        if (isRunning) {
                            System.err.println("Error accepting connection: " + e.getMessage());
                        }
                    }
                }
            }).start();

            // Main synchronization loop
            while (isRunning) {
                synchronizeClocks();
                Thread.sleep(10000); // Sync every 10 seconds
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isRunning = false;
            try {
                if (serverSocket != null) serverSocket.close();
                for (Socket socket : slaveConnections) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void synchronizeClocks() {
        try {
            List<Long> times = new ArrayList<>();
            List<String> nodeNames = new ArrayList<>();
            times.add(masterTime); // Include master's time
            nodeNames.add("Master");
            
            // Collect times from all slaves
            Set<Socket> disconnectedSlaves = new HashSet<>();
            Map<Socket, String> socketToName = new HashMap<>();
            
            for (Socket slaveSocket : slaveConnections) {
                try {
                    // Send GET_TIME message
                    ObjectOutputStream out = new ObjectOutputStream(slaveSocket.getOutputStream());
                    out.writeObject(new ClockMessage(ClockMessage.Type.GET_TIME, masterTime, "Master"));
                    
                    // Receive TIME_RESPONSE
                    ObjectInputStream in = new ObjectInputStream(slaveSocket.getInputStream());
                    ClockMessage response = (ClockMessage) in.readObject();
                    times.add(response.getTime());
                    nodeNames.add(response.getNodeName());
                    socketToName.put(slaveSocket, response.getNodeName());
                    System.out.println("Received time from " + response.getNodeName() + ": " + response.getTime());
                } catch (Exception e) {
                    System.err.println("Failed to communicate with slave: " + e.getMessage());
                    disconnectedSlaves.add(slaveSocket);
                }
            }
            
            // Remove disconnected slaves
            slaveConnections.removeAll(disconnectedSlaves);
            
            // Compute average time
            long sum = times.stream().mapToLong(Long::longValue).sum();
            long avg = sum / times.size();
            
            // Adjust master's time
            long masterOffset = avg - masterTime;
            masterTime += masterOffset;
            System.out.println("Master adjusted by " + masterOffset + ". New time: " + masterTime);
            
            // Send individual adjustments to slaves
            for (Socket slaveSocket : slaveConnections) {
                try {
                    // Find this slave's time and name
                    String slaveName = socketToName.get(slaveSocket);
                    int slaveIndex = nodeNames.indexOf(slaveName);
                    long slaveTime = times.get(slaveIndex);
                    
                    // Calculate this slave's individual offset
                    long slaveOffset = avg - slaveTime;
                    
                    ObjectOutputStream out = new ObjectOutputStream(slaveSocket.getOutputStream());
                    out.writeObject(new ClockMessage(ClockMessage.Type.ADJUST_TIME, slaveOffset, "Master"));
                    System.out.println("Sending offset " + slaveOffset + " to " + slaveName);
                } catch (Exception e) {
                    System.err.println("Failed to send adjustment to slave: " + e.getMessage());
                }
            }
            
            System.out.println("Synchronization complete. Average time: " + avg);
        } catch (Exception e) {
            System.err.println("Error during synchronization: " + e.getMessage());
        }
    }
}
