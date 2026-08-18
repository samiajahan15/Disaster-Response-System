package mesh;

public class HelloDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Disaster Mesh - HELLO discovery demo ===\n");


        Peer node1 = makeDemoPeer("NODE-1", "RESCUE_OFFICER");
        Peer node2 = makeDemoPeer("NODE-2", "HOSPITAL_LIAISON");
        Peer node3 = makeDemoPeer("NODE-3", "OPERATIONS_MONITOR");
        Peer[] peers = { node1, node2, node3 };


        for (Peer peer : peers) {
            peer.openSocket();
            new Thread(peer).start();
        }

        Thread.sleep(500);

        for (Peer peer : peers) {
            peer.sendHello();
        }

        Thread.sleep(1000);

        System.out.println("\n--- Discovered neighbors ---");
        for (Peer peer : peers) {
            System.out.println(peer.getNodeId() + " knows: " + peer.getNeighbors());
        }


        for (Peer peer : peers) {
            peer.stop();
        }
        System.out.println("\n=== Demo finished ===");
    }


    private static Peer makeDemoPeer(String nodeId, String role) {
        return new Peer(nodeId, role) {
            @Override
            protected void handleMessage(Message message) {

            }
        };
    }
}
