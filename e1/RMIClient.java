import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class RMIClient {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            RemoteInterface remote = (RemoteInterface) registry.lookup("RemoteService");
            Scanner scanner = new Scanner(System.in);
            Random random = new Random();

            while (true) {
                System.out.print("Enter array length (or 0 to exit): ");
                int length = scanner.nextInt();
                
                if (length <= 0) {
                    break;
                }

                // Generate random array
                int[] arr = new int[length];
                for (int i = 0; i < length; i++) {
                    arr[i] = random.nextInt(100); // Random numbers between 0 and 99
                }
                
                System.out.println("Original array: " + Arrays.toString(arr));

                // Sort the array using the remote service
                int[] sortedArr = remote.sortArray(arr);
                System.out.println("Sorted array: " + Arrays.toString(sortedArr));
                System.out.println();
            }
            
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
