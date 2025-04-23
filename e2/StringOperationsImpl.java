import StringOperations.StringService;
import StringOperations.StringServiceHelper;
import StringOperations.StringServiceOperations;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.Servant;

public class StringOperationsImpl extends Servant implements StringServiceOperations, InvokeHandler {
    private ORB orb;
    
    public void setORB(ORB orb) {
        this.orb = orb;
    }
    
    // Required method for Servant class
    public String[] _all_interfaces(POA poa, byte[] objectId) {
        return new String[] { StringServiceHelper.id() };
    }
    
    // Required method for InvokeHandler interface
    public OutputStream _invoke(String method, InputStream input, ResponseHandler handler) {
        OutputStream output = null;
        
        try {
            if (method.equals("concatenate")) {
                String str1 = input.read_string();
                String str2 = input.read_string();
                String result = concatenate(str1, str2);
                output = handler.createReply();
                output.write_string(result);
            } else if (method.equals("reverse")) {
                String str = input.read_string();
                String result = reverse(str);
                output = handler.createReply();
                output.write_string(result);
            } else if (method.equals("toUpperCase")) {
                String str = input.read_string();
                String result = toUpperCase(str);
                output = handler.createReply();
                output.write_string(result);
            } else if (method.equals("toLowerCase")) {
                String str = input.read_string();
                String result = toLowerCase(str);
                output = handler.createReply();
                output.write_string(result);
            } else if (method.equals("getLength")) {
                String str = input.read_string();
                int result = getLength(str);
                output = handler.createReply();
                output.write_long(result);
            } else if (method.equals("contains")) {
                String str = input.read_string();
                String substring = input.read_string();
                boolean result = contains(str, substring);
                output = handler.createReply();
                output.write_boolean(result);
            } else if (method.equals("substring")) {
                String str = input.read_string();
                int start = input.read_long();
                int end = input.read_long();
                String result = substring(str, start, end);
                output = handler.createReply();
                output.write_string(result);
            } else {
                throw new org.omg.CORBA.BAD_OPERATION();
            }
        } catch (Exception e) {
            output = handler.createExceptionReply();
            // Write exception information to the output stream
            // This is a simplified version - in a real implementation, you'd need to
            // properly marshal the exception
            System.err.println("Exception in _invoke: " + e.getMessage());
            e.printStackTrace();
        }
        
        return output;
    }
    
    public String concatenate(String str1, String str2) {
        System.out.println("(" + Thread.currentThread().getName() + ") : Concatenating strings: " + str1 + " and " + str2);
        return str1 + str2;
    }
    
    public String reverse(String str) {
        System.out.println("(" + Thread.currentThread().getName() + ") : Reversing string: " + str);
        return new StringBuilder(str).reverse().toString();
    }
    
    public String toUpperCase(String str) {
        System.out.println("(" + Thread.currentThread().getName() + ") : Converting to uppercase: " + str);
        return str.toUpperCase();
    }
    
    public String toLowerCase(String str) {
        System.out.println("(" + Thread.currentThread().getName() + ") : Converting to lowercase: " + str);
        return str.toLowerCase();
    }
    
    public int getLength(String str) {
        System.out.println("(" + Thread.currentThread().getName() + ") : Getting length of: " + str);
        return str.length();
    }
    
    public boolean contains(String str, String substring) {
        System.out.println("(" + Thread.currentThread().getName() + ") : Checking if " + str + " contains " + substring);
        return str.contains(substring);
    }
    
    public String substring(String str, int start, int end) {
        System.out.println("(" + Thread.currentThread().getName() + ") : Getting substring of " + str + " from " + start + " to " + end);
        return str.substring(start, end);
    }
} 