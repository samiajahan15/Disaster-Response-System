package mesh;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;


public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    private String id;
    private String originId;
    private String type;
    private int severity;
    private String payload;
    private int ttl;
    private String timestamp;


    public Message(String originId, String type, int severity, String payload, int ttl) {
        this.originId = originId;
        this.type = type;
        this.severity = severity;
        this.payload = payload;
        this.ttl = ttl;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        this.id = originId + "-" + SEQUENCE.incrementAndGet();
    }


    public Message(String id, String originId, String type, int severity,
                   String payload, int ttl, String timestamp) {
        this.id = id;
        this.originId = originId;
        this.type = type;
        this.severity = severity;
        this.payload = payload;
        this.ttl = ttl;
        this.timestamp = timestamp;
    }


    public void decrementTtl() {
        this.ttl--;
    }

    public boolean isExpired() {
        return this.ttl <= 0;
    }

    public String getId() {
        return id;
    }

    public String getOriginId() {
        return originId;
    }

    public String getType() {
        return type;
    }

    public int getSeverity() {
        return severity;
    }

    public String getPayload() {
        return payload;
    }

    public int getTtl() {
        return ttl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Message{id=" + id
             + ", origin=" + originId
             + ", type=" + type
             + ", severity=" + severity
             + ", ttl=" + ttl
             + ", payload='" + payload + "'"
             + ", time=" + timestamp + "}";
    }
}
