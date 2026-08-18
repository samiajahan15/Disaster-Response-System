package demo;
import mesh.*;

public class AckMessageDemo {

    public static void main(String[] args) {
        System.out.println("=== Disaster Mesh - ACK message test ===\n");

        SosMessage sos = new SosMessage(
                "NODE-A",
                "Tasnia",
                "Block C, 2nd floor",
                "FLOOD",
                9,
                3);
        System.out.println("SOS created by NODE-A:");
        System.out.println("   SOS id   : " + sos.getId());
        System.out.println("   Victim   : " + sos.getVictimName());
        System.out.println("   Severity : " + sos.getSeverity());


        AckMessage ack = new AckMessage(
                "NODE-B",
                sos.getId(),
                3);

        System.out.println("\nACK created by NODE-B:");
        System.out.println("   ACK id              : " + ack.getId());
        System.out.println("   Acknowledged SOS id : " + ack.getAcknowledgedMessageId());
        System.out.println("   Origin              : " + ack.getOriginId());
        System.out.println("   Type                : " + ack.getType());
        System.out.println("   TTL                 : " + ack.getTtl());


        System.out.println("   Severity (always 0) : " + ack.getSeverity());
        System.out.println("   Timestamp           : " + ack.getTimestamp());
        System.out.println("   Payload             : " + ack.getPayload());


        Message asMessage = ack;
        System.out.println("\nUsed as a Message   : " + asMessage.getType()
                + " (id " + asMessage.getId() + ")");


        boolean matches = ack.getAcknowledgedMessageId().equals(sos.getId());
        System.out.println("ACK matches the SOS : " + matches);

        System.out.println("\nFull toString: " + ack);
        System.out.println("\n=== Test finished ===");
    }
}
