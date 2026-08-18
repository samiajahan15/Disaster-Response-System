package mesh;


public class SosMessage extends Message {
    private static final long serialVersionUID = 1L;

    private String victimName;
    private String location;
    private String emergencyType;


    public SosMessage(String originId, String victimName, String location,
                      String emergencyType, int severity, int ttl) {
        super(originId, "SOS", severity,
              "SOS from " + victimName + " at " + location + " (" + emergencyType + ")",
              ttl);
        this.victimName = victimName;
        this.location = location;
        this.emergencyType = emergencyType;
    }



    public String getVictimName() {
        return victimName;
    }

    public String getLocation() {
        return location;
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    @Override
    public String toString() {
        return "SosMessage{id=" + getId()
             + ", origin=" + getOriginId()
             + ", victim='" + victimName + "'"
             + ", location='" + location + "'"
             + ", emergencyType=" + emergencyType
             + ", severity=" + getSeverity()
             + ", ttl=" + getTtl()
             + ", time=" + getTimestamp() + "}";
    }
}
