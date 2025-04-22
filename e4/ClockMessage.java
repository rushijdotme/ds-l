import java.io.Serializable;

public class ClockMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum Type {
        GET_TIME,
        TIME_RESPONSE,
        ADJUST_TIME,
        SYNC_COMPLETE
    }
    
    private Type type;
    private long time;
    private String nodeName;
    
    public ClockMessage(Type type, long time, String nodeName) {
        this.type = type;
        this.time = time;
        this.nodeName = nodeName;
    }
    
    public Type getType() {
        return type;
    }
    
    public long getTime() {
        return time;
    }
    
    public String getNodeName() {
        return nodeName;
    }
} 