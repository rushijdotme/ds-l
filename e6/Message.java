import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum Type {
        ELECTION,    // Used in Bully algorithm
        OK,          // Used in Bully algorithm
        COORDINATOR, // Used in Bully algorithm
        TOKEN,       // Used in Ring algorithm
        LEADER       // Used in Ring algorithm
    }
    
    private final Type type;
    private final int senderId;
    private final int receiverId;
    private final int data;  // Additional data (e.g., election number, token value)
    
    public Message(Type type, int senderId, int receiverId, int data) {
        this.type = type;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.data = data;
    }
    
    public Type getType() {
        return type;
    }
    
    public int getSenderId() {
        return senderId;
    }
    
    public int getReceiverId() {
        return receiverId;
    }
    
    public int getData() {
        return data;
    }
    
    @Override
    public String toString() {
        return String.format("Message[type=%s, from=%d, to=%d, data=%d]", 
            type, senderId, receiverId, data);
    }
} 