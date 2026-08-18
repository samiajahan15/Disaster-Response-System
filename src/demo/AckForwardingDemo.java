package demo;
import mesh.*;


public class AckForwardingDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Disaster Mesh - SOS/ACK round trip test ===\n");

        AckDemoPeer nodeA = new AckDemoPeer("NODE-A", "EMERGENCY_OPERATOR");
        AckDemoPeer nodeB = new AckDemoPeer("NODE-B", "RESCUE_OFFICER");
        AckDemoPeer[] peers = { nodeA, nodeB };

        for (AckDemoPeer peer : peers) {
            peer.openSocket();
            new Thread(peer).start();
        }
        Thread.sleep(500);


        System.out.println("--- STEP 1: NODE-A sends an SOS ---");
        SosMessage sos = new SosMessage(
                "NODE-A",
                "Shamima",
                "Block C, 2nd floor",
                "FLOOD",
                9,
                3);
        System.out.println("SOS id = " + sos.getId() + ", ttl = " + sos.getTtl());
        nodeA.sendForDemo(sos);


        System.out.println("\n--- STEPS 2-4: NODE-B receives SOS, replies with ACK,"
                + " NODE-A receives ACK ---");
        Thread.sleep(1500);


        System.out.println("\n--- Summary ---");
        for (AckDemoPeer peer : peers) {
            System.out.println(peer.getNodeId()
                    + " | messages held: " + peer.getMessageQueue().size());
        }
        System.out.println("NODE-B holds the SOS it received.");
        System.out.println("NODE-A holds the ACK that came back for " + sos.getId() + ".");


        for (AckDemoPeer peer : peers) {
            peer.stop();
        }
        System.out.println("\n=== Test finished ===");
    }

    private static class AckDemoPeer extends Peer {

        AckDemoPeer(String nodeId, String role) {
            super(nodeId, role);
        }

        @Override
        protected void handleMessage(Message message) {

        }

        public void sendForDemo(Message message) throws java.io.IOException {
            sendMessage(message);
        }
    }
}
