import StringOperations.StringService;
import StringOperations.StringServiceHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;

public class StringClient {
    public static void main(String[] args) {
        try {
            // Initialize the ORB
            ORB orb = ORB.init(args, null);
            
            // Get the root naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            
            // Resolve the object reference in naming
            String name = "StringService";
            StringService stringService = null;
            try {
                stringService = StringServiceHelper.narrow(ncRef.resolve_str(name));
            } catch (NotFound | CannotProceed | InvalidName e) {
                System.err.println("(" + Thread.currentThread().getName() + ") : ERROR resolving service: " + e);
                e.printStackTrace(System.out);
                return;
            }
            
            if (stringService == null) {
                System.err.println("(" + Thread.currentThread().getName() + ") : ERROR: Could not resolve StringService");
                return;
            }
            
            // Test string operations
            String str1 = "Hello";
            String str2 = "World";
            
            System.out.println("(" + Thread.currentThread().getName() + ") : Testing string operations...");
            
            // Test concatenation
            String result = stringService.concatenate(str1, str2);
            System.out.println("(" + Thread.currentThread().getName() + ") : Concatenation result: " + result);
            
            // Test reverse
            result = stringService.reverse(result);
            System.out.println("(" + Thread.currentThread().getName() + ") : Reverse result: " + result);
            
            // Test toUpperCase
            result = stringService.toUpperCase(result);
            System.out.println("(" + Thread.currentThread().getName() + ") : Uppercase result: " + result);
            
            // Test toLowerCase
            result = stringService.toLowerCase(result);
            System.out.println("(" + Thread.currentThread().getName() + ") : Lowercase result: " + result);
            
            // Test getLength
            int length = stringService.getLength(result);
            System.out.println("(" + Thread.currentThread().getName() + ") : Length: " + length);
            
            // Test contains
            boolean contains = stringService.contains(result, "world");
            System.out.println("(" + Thread.currentThread().getName() + ") : Contains 'world': " + contains);
            
            // Test substring
            result = stringService.substring(result, 0, 5);
            System.out.println("(" + Thread.currentThread().getName() + ") : Substring result: " + result);
            
            // Shutdown the ORB
            orb.shutdown(true);
            
        } catch (Exception e) {
            System.err.println("(" + Thread.currentThread().getName() + ") : ERROR: " + e);
            e.printStackTrace(System.out);
        }
    }
} 