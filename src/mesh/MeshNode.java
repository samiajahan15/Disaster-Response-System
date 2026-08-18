package mesh;

import interfaces.Notifiable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public abstract class MeshNode extends Peer implements Notifiable {

    private final MeshRouter router;

    private final boolean forwardingEnabled;

    private final Queue<Message> pendingOutbox = new ConcurrentLinkedQueue<>();

    private final Set<String> sentMessageIds = ConcurrentHashMap.newKeySet();

    private final List<String> acknowledgements = new ArrayList<>();

    private volatile boolean newPeerAvailable = false;


    public MeshNode(String nodeId, String role) {
        this(nodeId, role, true);
    }


    public MeshNode(String nodeId, String role, boolean forwardingEnabled) {
        super(nodeId, role);
        this.forwardingEnabled = forwardingEnabled;
        this.router = new MeshRouter(this);
    }


    @Override
    protected void onMessageReceived(Message message) {

        flushPending();


        super.onMessageReceived(message);


        if (message instanceof AckMessage) {
            AckMessage ack = (AckMessage) message;
            if (sentMessageIds.contains(ack.getAcknowledgedMessageId())) {
                String record = ack.getOriginId() + " acknowledged " + ack.getAcknowledgedMessageId();
                synchronized (acknowledgements) {
                    acknowledgements.add(record);
                }
                System.out.println(receiveNotification("CONFIRMED - " + record));
            }
        }


        if (forwardingEnabled && !"HELLO".equals(message.getType())) {
            router.route(message);
        }
    }


    @Override
    protected void sendMessage(Message message) throws IOException {

        if (nodeId.equals(message.getOriginId())) {
            sentMessageIds.add(message.getId());
        }

        if (!isLinkAvailable()) {

            if ("HELLO".equals(message.getType())) {
                return;
            }
            pendingOutbox.add(message);
            log("No link right now - stored " + message.getId()
                    + " locally (" + pendingOutbox.size() + " waiting)");
            return;
        }
        try {
            super.sendMessage(message);
        } catch (IOException e) {

            pendingOutbox.add(message);
            log("Send failed (" + e.getMessage() + ") - stored "
                    + message.getId() + " locally");
        }
    }


    public void flushPending() {
        if (pendingOutbox.isEmpty() || !isLinkAvailable()) {
            return;
        }
        int delivered = 0;
        while (!pendingOutbox.isEmpty()) {
            Message message = pendingOutbox.peek();
            try {
                super.sendMessage(message);
                pendingOutbox.poll();
                delivered++;
            } catch (IOException e) {
                log("Still cannot send " + message.getId() + " - keeping it stored");
                break;
            }
        }
        if (delivered > 0) {
            log("Link available again - delivered " + delivered + " stored message(s)");
        }
    }


    protected boolean isLinkAvailable() {
        return socket != null && !socket.isClosed() && !neighbors.isEmpty();
    }


    @Override
    protected void onNewNeighbor(String neighborId) {
        newPeerAvailable = true;
        log("New peer available: " + neighborId
                + " - will pass on anything I am still carrying");
    }


    @Override
    protected void onRetryTick() {
        flushPending();

        if (newPeerAvailable) {
            newPeerAvailable = false;
            relayStoredMessages();
        }
    }


    private void relayStoredMessages() {
        for (Message message : getMessageQueue()) {
            if (message.isExpired() || nodeId.equals(message.getOriginId())) {
                continue;
            }
            try {
                log("Relaying stored " + message.getType() + " " + message.getId()
                        + " (ttl " + message.getTtl() + ") to the peers available now");
                sendMessage(message);
            } catch (IOException e) {
                log("Could not relay " + message.getId() + ": " + e.getMessage());
            }
        }
    }


    @Override
    public String receiveNotification(String message) {
        return "[" + getNodeId() + "] NOTIFICATION: " + message;
    }


    public int getPendingCount() {
        return pendingOutbox.size();
    }


    public List<String> getAcknowledgements() {
        synchronized (acknowledgements) {
            return new ArrayList<>(acknowledgements);
        }
    }


    public boolean isForwardingEnabled() {
        return forwardingEnabled;
    }
    protected void handleMessage(Message message) {
        if ("CHAT".equals(message.getType())) {
            String payload = message.getPayload();
            int separator = payload.indexOf("|");
            String recipient = separator >= 0 ? payload.substring(0, separator) : "ALL";
            String text = separator >= 0 ? payload.substring(separator + 1) : payload;
            if (recipient.startsWith("TO=")) {
                recipient = recipient.substring(3);
            }
            if ("ALL".equalsIgnoreCase(recipient) || nodeId.equalsIgnoreCase(recipient)) {
                System.out.println();
                System.out.println("========================================");
                System.out.println("INCOMING MESH MESSAGE");
                System.out.println("From: " + message.getOriginId());
                System.out.println("To: " + recipient);
                System.out.println("Message: " + text);
                System.out.println("========================================");
                System.out.print("Select option: ");
            }
        }
    }

    public void sendSos(SosMessage sos) throws IOException {
        sendMessage(sos);
    }

    public void sendChat(String recipient, String text) throws IOException {
        sendMessage(new Message(nodeId, "CHAT", 0, "TO=" + recipient + "|" + text, 3));
    }

}
