
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Master {

    public static void main(String[] args) {
        try {
            int numWorkers = Integer.parseInt(args[0]);
            int[] array = {1, 2, 3, 4, 5, 6, 7, 8}; // Example array
            int chunkSize = array.length / numWorkers;
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            int totalSum = 0;
            for (int i = 0; i < numWorkers; i++) {
                int start = i * chunkSize;
                int end = (i == numWorkers - 1) ? array.length : start + chunkSize;
                int[] subArray = new int[end - start];
                System.arraycopy(array, start, subArray, 0, end - start);
                String workerName = "W" + (i + 1);
                Worker worker = (Worker) registry.lookup(workerName);
                int partialSum = worker.calculateSum(subArray);
                totalSum += partialSum;
                System.out.println(workerName + " calculated sum: " + partialSum);
            }
            System.out.println("Total sum: " + totalSum);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
