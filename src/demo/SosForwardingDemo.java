
package demo;
import mesh.*;

public class SosForwardingDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Disaster Mesh - SOS forwarding test ===\n");

        SosDemoPeer nodeA = new SosDemoPeer("NODE-A", "EMERGENCY_OPERATOR");
        SosDemoPeer nodeB = new SosDemoPeer("NODE-B", "RESCUE_OFFICER");
        SosDemoPeer nodeC = new SosDemoPeer("NODE-C", "HOSPITAL_LIAISON");
        SosDemoPeer[] peers = { nodeA, nodeB, nodeC };

        MeshRouter routerB = new MeshRouter(nodeB);
        MeshRouter routerC = new MeshRouter(nodeC);
        nodeB.setRouter(routerB);
        nodeC.setRouter(routerC);

        for (SosDemoPeer peer : peers) {
            peer.openSocket();
            new Thread(peer).start();
        }
        Thread.sleep(500);

        System.out.println("--- STEP 1: NODE-A creates and broadcasts an SOS (ttl=3) ---");
        SosMessage sos = new SosMessage(
                "NODE-A",
                "Arundhuti",
                "Block C, 2nd floor",
                "FLOOD",
                9,
                3);
        System.out.println("Created: id=" + sos.getId() + ", ttl=" + sos.getTtl());
        nodeA.sendForDemo(sos);
        System.out.println("NODE-A broadcast the SOS.\n");

        Thread.sleep(1500);

        System.out.println("\n--- STEP 2: NODE-A broadcasts the SAME SOS again (duplicate) ---");
        System.out.println("Queue sizes before: NODE-B=" + nodeB.getMessageQueue().size()
                + ", NODE-C=" + nodeC.getMessageQueue().size());
        nodeA.sendForDemo(sos);
        Thread.sleep(1000);
        System.out.println("Queue sizes after : NODE-B=" + nodeB.getMessageQueue().size()
                + ", NODE-C=" + nodeC.getMessageQueue().size());
        System.out.println("(unchanged = the duplicate was ignored, nothing re-displayed)");

        System.out.println("\nAsking NODE-B's router to route the same SOS again:");
        boolean forwardedAgain = routerB.route(sos);
        System.out.println("   result: " + (forwardedAgain ? "FORWARDED" : "DROPPED")
                + "  (expected DROPPED - already seen)");

        System.out.println("\n--- Summary ---");
        for (SosDemoPeer peer : peers) {
            Message stored = peer.getMessageQueue().peek();
            System.out.println(peer.getNodeId()
                    + " | messages held: " + peer.getMessageQueue().size()
                    + " | ttl of held SOS: " + (stored == null ? "-" : stored.getTtl()));
        }
        System.out.println("(NODE-A holds none: a peer ignores its own broadcast.)");
        System.out.println("(ttl 3 -> 2 shows one hop was used before forwarding.)");

        for (SosDemoPeer peer : peers) {
            peer.stop();
        }
        System.out.println("\n=== Test finished ===");
    }


    private static class SosDemoPeer extends Peer {

        private MeshRouter router;

        SosDemoPeer(String nodeId, String role) {
            super(nodeId, role);
        }

        void setRouter(MeshRouter router) {
            this.router = router;
        }


        public void sendForDemo(Message message) throws java.io.IOException {
            sendMessage(message);
        }

        @Override
        protected void handleMessage(Message message) {

            if (message instanceof SosMessage) {
                SosMessage sos = (SosMessage) message;


                String details =
                      "[" + getNodeId() + "] SOS RECEIVED\n"
                    + "[" + getNodeId() + "]    Victim        : " + sos.getVictimName() + "\n"
                    + "[" + getNodeId() + "]    Location      : " + sos.getLocation() + "\n"
                    + "[" + getNodeId() + "]    Emergency type: " + sos.getEmergencyType() + "\n"
                    + "[" + getNodeId() + "]    Severity      : " + sos.getSeverity() + "\n"
                    + "[" + getNodeId() + "]    TTL on arrival: " + sos.getTtl();
                System.out.println(details);


                if (router != null) {
                    router.route(sos);
                }
            } else {
                log("Received non-SOS message: " + message.getId());
            }
        }
    }
}
