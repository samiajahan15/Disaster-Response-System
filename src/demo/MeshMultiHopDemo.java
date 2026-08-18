package demo;
import mesh.*;

import model.Hospital;
import model.RescueTeam;
import util.Constants;

import java.util.List;
import java.util.Set;

public class MeshMultiHopDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== DISASTER MESH - MULTI-HOP STORE-AND-FORWARD TEST ===");
        System.out.println("Topology:  A (USER-1) ---- B (RELAY-1) ---- C (MEDIC-1)");
        System.out.println("C is NOT running when A sends its SOS, so only B can carry it.");
        System.out.println("Demo-level rule: A and C are logically NOT allowed to be direct");
        System.out.println("neighbours - only RELAY-1 may bridge them (see class comment).");

        UserNode userA = new UserNode("USER-1", Set.of("MEDIC-1"));
        RelayNode relayB = new RelayNode("RELAY-1");

        RescueTeam alphaSquad = new RescueTeam(
                "RT001", "Alpha Squad", "FLOOD_RESCUE", 8, "U002");
        RescueNode rescueB = new RescueNode("RESCUE-1", alphaSquad);

        Hospital cityGeneral = new Hospital(
                "H001", "City General Hospital", 120, 20, 45,
                "Ambulances:5;Ventilators:10", "U004");
        RangeLimitedMedicNode medicC =
                new RangeLimitedMedicNode("MEDIC-1", cityGeneral, Set.of("USER-1"));

        section("STEP 1 - only A and B are switched on (C is offline)");

        relayB.openSocket();
        new Thread(relayB).start();
        userA.openSocket();
        new Thread(userA).start();
        Thread.sleep(1200);

        System.out.println("USER-1 neighbours : " + userA.getNeighbors());
        System.out.println("RELAY-1 neighbours: " + relayB.getNeighbors());
        System.out.println("MEDIC-1 is still OFFLINE.");

        section("STEP 2 - A creates an SOS; only B can hear it");

        SosMessage sos = new SosMessage(
                "USER-1",
                "Ayon",
                "Block C, 2nd floor",
                Constants.TYPE_FLOOD,
                9,
                3);

        System.out.println("SOS ID   : " + sos.getId());
        System.out.println("Victim   : " + sos.getVictimName());
        System.out.println("Location : " + sos.getLocation());
        System.out.println("Severity : " + sos.getSeverity());
        System.out.println("TTL      : " + sos.getTtl() + " hops\n");

        userA.sendForDemo(sos);
        Thread.sleep(1200);

        System.out.println("\nRELAY-1 is now carrying " + relayB.getMessageQueue().size()
                + " message(s). MEDIC-1 has received nothing - it was offline.");

        section("STEP 3 - C (the medical peer) comes within range");
        System.out.println("B's discovery thread will hear C's HELLO, and B's relay"
                + " thread\nwill then pass on the SOS it has been carrying.");
        System.out.println("USER-1 and MEDIC-1 will each ignore the other's HELLO");
        System.out.println("(demo-level filter), so they never become direct neighbours.\n");

        medicC.openSocket();
        new Thread(medicC).start();

        rescueB.openSocket();
        new Thread(rescueB).start();

        Thread.sleep(3000);

        section("RESULTS");

        boolean medicGotSos = false;
        for (Message message : medicC.getMessageQueue()) {
            if (message.getId().equals(sos.getId())) {
                medicGotSos = true;
            }
        }
        System.out.println("6-7. B relayed the stored SOS and C received it: "
                + (medicGotSos ? "YES" : "NO"));

        boolean medicSentAck = false;
        for (Message message : userA.getMessageQueue()) {
            if (message instanceof AckMessage
                    && "MEDIC-1".equals(message.getOriginId())
                    && ((AckMessage) message).getAcknowledgedMessageId().equals(sos.getId())) {
                medicSentAck = true;
            }
        }

        List<String> confirmations = userA.getAcknowledgements();
        boolean ackReachedUser = !confirmations.isEmpty();
        System.out.println("8. C sent an ACK for " + sos.getId() + ": "
                + (medicSentAck ? "YES" : "NO"));
        System.out.println("9-10. B relayed the ACK and A received it back: "
                + (ackReachedUser ? "YES (multi-hop ACK)" : "NO"));

        System.out.println("\nAcknowledgements that reached the victim's device:");
        if (confirmations.isEmpty()) {
            System.out.println("   (none)");
        } else {
            for (String confirmation : confirmations) {
                System.out.println("   USER-1 received ACK -> " + confirmation);
            }
        }

        System.out.println("\nPer-node statistics:");
        MeshNode[] nodes = { userA, relayB, rescueB, medicC };
        for (MeshNode node : nodes) {
            System.out.println("   " + String.format("%-10s", node.getNodeId())
                    + " neighbours=" + node.getNeighbors().size()
                    + "  carrying=" + node.getMessageQueue().size()
                    + "  waiting=" + node.getPendingCount()
                    + "  duplicatesDropped=" + node.getDuplicatesSuppressed());
        }

        boolean userSeesMedicDirectly = userA.getNeighbors().contains("MEDIC-1");
        boolean medicSeesUserDirectly = medicC.getNeighbors().contains("USER-1");
        boolean directPathUsed = userSeesMedicDirectly || medicSeesUserDirectly;

        System.out.println("\nUSER-1 neighbours : " + userA.getNeighbors());
        System.out.println("MEDIC-1 neighbours: " + medicC.getNeighbors());
        System.out.println("11. A and C logically prevented from being direct neighbours: "
                + (!directPathUsed ? "YES" : "NO (topology filter failed!)"));

        System.out.println("\nThe SOS reached the medical peer only because RELAY-1");
        System.out.println("stored it and passed it on later. No central server exists.");

        section("FINAL RESULT");
        System.out.println("SOS reached C through RELAY-1: " + (medicGotSos ? "YES" : "NO"));
        System.out.println("ACK reached A through RELAY-1: " + (ackReachedUser ? "YES" : "NO"));
        System.out.println("Direct A-C path used: " + (directPathUsed ? "YES" : "NO"));
        System.out.println("Central server used: NO");

        section("SHUTDOWN - listener, discovery and relay threads must all stop");

        for (MeshNode node : nodes) {
            node.stop();
        }
        Thread.sleep(400);

        int meshThreadsAlive = 0;
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            String name = thread.getName();
            if ((name.contains("-discovery") || name.contains("-relay")) && thread.isAlive()) {
                meshThreadsAlive++;
                System.out.println("   still alive: " + name);
            }
        }
        System.out.println("Background mesh threads still running: " + meshThreadsAlive
                + (meshThreadsAlive == 0 ? "  (clean shutdown)" : ""));

        System.out.println("\n=== MULTI-HOP TEST COMPLETE ===");
    }

    private static void section(String title) {
        System.out.println("\n--------------------------------------------------");
        System.out.println(title);
        System.out.println("--------------------------------------------------");
    }


    private static class UserNode extends MeshNode {

        private final Set<String> outOfRange;

        UserNode(String nodeId, Set<String> outOfRange) {
            super(nodeId, Constants.ROLE_OPERATOR);
            this.outOfRange = outOfRange;
        }

        @Override
        protected void onMessageReceived(Message message) {
            if ("HELLO".equals(message.getType()) && outOfRange.contains(message.getOriginId())) {
                log("Ignoring HELLO from " + message.getOriginId()
                        + " - out of local range in this simulation (demo-level topology filter)");
                return;
            }
            super.onMessageReceived(message);
        }

        public void sendForDemo(Message message) throws java.io.IOException {
            sendMessage(message);
        }

        @Override
        protected void handleMessage(Message message) {
        }
    }


    private static class RangeLimitedMedicNode extends HospitalNode {

        private final Set<String> outOfRange;

        RangeLimitedMedicNode(String nodeId, Hospital hospital, Set<String> outOfRange) {
            super(nodeId, hospital);
            this.outOfRange = outOfRange;
        }

        @Override
        protected void onMessageReceived(Message message) {
            if ("HELLO".equals(message.getType()) && outOfRange.contains(message.getOriginId())) {
                log("Ignoring HELLO from " + message.getOriginId()
                        + " - out of local range in this simulation (demo-level topology filter)");
                return;
            }
            super.onMessageReceived(message);
        }
    }
}
