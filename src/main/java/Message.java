import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private String sender;
    private String content;
    private Date timestamp;

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
        this.timestamp = new Date();
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return sender + ": " + content;
    }
}