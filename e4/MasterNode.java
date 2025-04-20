
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class MasterNode {

    private static final String[] SLAVE_NAMES = {"Slave1", "Slave2", "Slave3"};
    private static long masterTime;

    public static void main(String[] args) {
        try {
            // Set master's initial time (default: 0 for testing)
            masterTime = (args.length > 0) ? Long.parseLong(args[0]) : 0;
            Registry registry = LocateRegistry.getRegistry();
            while (true) {
                List<ClockSync> slaves = new ArrayList<>();
                List<Long> times = new ArrayList<>();
                // Include master's time in the calculation
                times.add(masterTime);
                // Collect slave times
                for (String name : SLAVE_NAMES) {
                    try {
                        ClockSync slave = (ClockSync) registry.lookup(name);
                        slaves.add(slave);
                        times.add(slave.getTime());
                        System.out.println("Fetched time from " + name + ": "
                                + times.get(times.size() - 1));
                    } catch (Exception e) {
                        System.err.println("Failed to connect to " + name);
                    }
                }
                // Compute average time
                long sum = times.stream().mapToLong(Long::longValue).sum();
                long avg = sum / times.size();
                // Adjust master's time
                long masterOffset = avg - masterTime;
                masterTime += masterOffset;
                System.out.println("Master adjusted by " + masterOffset + ". New time: "
                        + masterTime);
                // Send adjustments to slaves
                for (int i = 0; i < slaves.size(); i++) {
                    long slaveTime = times.get(i + 1); // Skip master's time
                    long offset = avg - slaveTime;
                    slaves.get(i).adjustTime(offset);
                }
                System.out.println("Synchronization complete. Average time: " + avg);
                Thread.sleep(10000); // Sync every 10 seconds
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
