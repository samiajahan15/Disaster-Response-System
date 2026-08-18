package demo;
import mesh.*;


public class SosMessageDemo {

    public static void main(String[] args) {
        System.out.println("=== Disaster Mesh - SOS message test ===\n");


        SosMessage sos = new SosMessage(
                "NODE-1",
                "Samia",
                "Block C, 2nd floor",
                "FLOOD",
                9,
                5);


        System.out.println("Victim name   : " + sos.getVictimName());
        System.out.println("Location      : " + sos.getLocation());
        System.out.println("Emergency type: " + sos.getEmergencyType());

        System.out.println("Message id    : " + sos.getId());
        System.out.println("Origin node   : " + sos.getOriginId());
        System.out.println("Type          : " + sos.getType());
        System.out.println("Severity      : " + sos.getSeverity());
        System.out.println("TTL           : " + sos.getTtl());
        System.out.println("Timestamp     : " + sos.getTimestamp());
        System.out.println("Payload       : " + sos.getPayload());

        Message asMessage = sos;
        System.out.println("\nAs a Message  : " + asMessage);
        System.out.println("Full toString : " + sos);

        System.out.println("\n=== Test finished ===");
    }
}
