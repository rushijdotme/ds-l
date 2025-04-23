import StringOperations.StringService;
import StringOperations.StringServiceHelper;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManager;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.PortableServer.Servant;

public class StringServer {
    public static void main(String[] args) {
        try {
            // Initialize the ORB
            ORB orb = ORB.init(args, null);
            
            // Get reference to root POA
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            
            // Activate the POA manager
            rootPOA.the_POAManager().activate();
            
            // Create the servant
            StringOperationsImpl stringService = new StringOperationsImpl();
            stringService.setORB(orb);
            
            // Get object reference from the servant
            StringService href = StringServiceHelper.narrow(rootPOA.servant_to_reference(stringService));
            
            // Get the root naming context
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            
            // Bind the object reference in naming
            String name = "StringService";
            NameComponent[] path = ncRef.to_name(name);
            try {
                ncRef.rebind(path, href);
            } catch (NotFound | CannotProceed | InvalidName e) {
                System.err.println("(" + Thread.currentThread().getName() + ") : ERROR binding to naming service: " + e);
                e.printStackTrace(System.out);
                return;
            }
            
            System.out.println("(" + Thread.currentThread().getName() + ") : StringService is ready and waiting...");
            System.out.println("(" + Thread.currentThread().getName() + ") : Press Ctrl+C to exit");
            
            // Wait for invocations from clients
            orb.run();
            
        } catch (Exception e) {
            System.err.println("(" + Thread.currentThread().getName() + ") : ERROR: " + e);
            e.printStackTrace(System.out);
        }
    }
} 