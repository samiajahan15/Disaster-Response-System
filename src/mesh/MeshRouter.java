package mesh;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class MeshRouter {


    private final Peer peer;


    private final Set<String> seenMessageIds = ConcurrentHashMap.newKeySet();


    public MeshRouter(Peer peer) {
        this.peer = peer;
    }


    public boolean route(Message message) {
        if (message == null) {
            return false;
        }

        if (seenMessageIds.contains(message.getId())) {
            log("DROP (already seen) " + message.getId());
            return false;
        }
        seenMessageIds.add(message.getId());

        if (message.isExpired()) {
            log("DROP (ttl expired) " + message.getId());
            return false;
        }

        message.decrementTtl();

        if (message.isExpired()) {
            log("DROP (ttl reached 0 after decrement) " + message.getId());
            return false;
        }

        forward(message);
        return true;
    }


    public boolean hasSeen(String messageId) {
        return seenMessageIds.contains(messageId);
    }


    private void forward(Message message) {
        try {
            peer.sendMessage(message);
            log("FORWARD " + message.getId() + " (ttl now " + message.getTtl() + ")");
        } catch (IOException e) {
            log("Failed to forward " + message.getId() + ": " + e.getMessage());
        }
    }


    private void log(String text) {
        System.out.println("[" + peer.getNodeId() + " router] " + text);
    }
}
