package protocol;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String type;
    private String sender;
    private String content;
    private String timestamp;

    public Message(String type, String sender, String content) {
        this.type = (type == null) ? "INFO" : type;
        this.sender = (sender == null) ? "SERVER" : sender;
        this.content = (content == null) ? "" : content;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    public Message(String type, String content) {
        this(type, "SERVER", content);
    }


    public String format() {
        return "[" + timestamp + "] (" + type + ") " + sender + ": " + content;
    }


    public String getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return format();
    }
}
