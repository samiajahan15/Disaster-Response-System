package mesh;

public class AckMessage extends Message {
    private static final long serialVersionUID = 1L;


    private String acknowledgedMessageId;


    public AckMessage(String originId, String acknowledgedMessageId, int ttl) {
        super(originId, "ACK", 0,
              "ACK for " + acknowledgedMessageId,
              ttl);
        this.acknowledgedMessageId = acknowledgedMessageId;
    }


    public String getAcknowledgedMessageId() {
        return acknowledgedMessageId;
    }

    @Override
    public String toString() {
        return "AckMessage{id=" + getId()
             + ", origin=" + getOriginId()
             + ", acknowledges=" + acknowledgedMessageId
             + ", type=" + getType()
             + ", severity=" + getSeverity()
             + ", ttl=" + getTtl()
             + ", time=" + getTimestamp() + "}";
    }
}
