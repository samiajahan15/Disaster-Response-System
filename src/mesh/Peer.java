package mesh;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class Peer implements Runnable {

    protected static final String MESH_GROUP = "230.0.0.1";
    protected static final int MESH_PORT = 5555;
    protected static final int BUFFER_SIZE = 8192;
    protected static final int HELLO_TTL = 1;
    protected static final int ACK_TTL = 3;
    protected static final int HELLO_INTERVAL_MS = 1500;
    protected static final int RETRY_INTERVAL_MS = 800;

    protected final String nodeId;
    protected final String role;
    protected MulticastSocket socket;


    protected final Set<String> neighbors = ConcurrentHashMap.newKeySet();


    protected final Set<String> seenMessageIds = ConcurrentHashMap.newKeySet();


    protected final Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();


    protected volatile boolean running = false;


    protected final AtomicInteger duplicatesSuppressed = new AtomicInteger(0);


    private Thread discoveryThread;
    private Thread relayThread;


    public Peer(String nodeId, String role) {
        this.nodeId = nodeId;
        this.role = role;
    }


    public synchronized void openSocket() throws IOException {
        if (socket != null && !socket.isClosed()) {
            return;
        }
        InetAddress group = InetAddress.getByName(MESH_GROUP);
        InetSocketAddress groupAddress = new InetSocketAddress(group, MESH_PORT);


        socket = new MulticastSocket(MESH_PORT);


        NetworkInterface nic = findMulticastInterface();
        if (nic != null) {
            socket.setNetworkInterface(nic);
            socket.joinGroup(groupAddress, nic);
        } else {
            socket.joinGroup(group);
        }
        running = true;
        log("Joined mesh group " + MESH_GROUP + ":" + MESH_PORT + " as role " + role);
    }


    @Override
    public void run() {
        try {
            openSocket();
            startHelperThreads();
            byte[] buffer = new byte[BUFFER_SIZE];

            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                Message message = deserialize(packet.getData(), packet.getLength());
                if (message == null) {
                    continue;
                }

                if (nodeId.equals(message.getOriginId())) {
                    continue;
                }

                if (!seenMessageIds.add(message.getId())) {
                    duplicatesSuppressed.incrementAndGet();
                    log("Duplicate suppressed: " + message.getId() + " (already handled)");
                    continue;
                }
                onMessageReceived(message);
            }
        } catch (SocketException e) {

            if (running) {
                log("Socket error: " + e.getMessage());
            }
        } catch (IOException e) {
            log("Receive error: " + e.getMessage());
        } finally {
            close();
        }
    }


    private synchronized void startHelperThreads() {
        if (discoveryThread == null) {
            discoveryThread = new Thread(this::discoveryLoop, nodeId + "-discovery");
            discoveryThread.setDaemon(true);
            discoveryThread.start();
        }
        if (relayThread == null) {
            relayThread = new Thread(this::relayLoop, nodeId + "-relay");
            relayThread.setDaemon(true);
            relayThread.start();
        }
    }


    private void discoveryLoop() {
        while (running) {
            try {
                sendHello();
                Thread.sleep(HELLO_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (IOException e) {

                if (running) {
                    log("Discovery could not send HELLO: " + e.getMessage());
                }
            }
        }
    }


    private void relayLoop() {
        while (running) {
            try {
                onRetryTick();
                Thread.sleep(RETRY_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


    protected void onRetryTick() {

    }


    protected void onNewNeighbor(String neighborId) {
    }


    protected void onMessageReceived(Message message) {
        if ("HELLO".equals(message.getType())) {
            boolean isNew = neighbors.add(message.getOriginId());
            if (isNew) {
                log("Peer discovered: " + message.getOriginId());
                onNewNeighbor(message.getOriginId());
            }
        } else {

            messageQueue.add(message);


            if (message instanceof SosMessage) {
                log("SOS received from " + message.getOriginId()
                        + " (message id " + message.getId() + ")");
                sendAck((SosMessage) message);
            } else if (message instanceof AckMessage) {
                AckMessage ack = (AckMessage) message;
                log("ACK received from " + ack.getOriginId()
                        + " for message " + ack.getAcknowledgedMessageId());
            }

            handleMessage(message);
        }
    }


    protected void sendAck(SosMessage sos) {
        try {
            AckMessage ack = new AckMessage(nodeId, sos.getId(), ACK_TTL);
            log("Sending ACK for " + sos.getId());
            sendMessage(ack);
        } catch (IOException e) {
            log("Failed to send ACK: " + e.getMessage());
        }
    }


    public void sendHello() throws IOException {
        Message hello = new Message(nodeId, "HELLO", 0, role, HELLO_TTL);
        sendMessage(hello);
    }


    protected void sendMessage(Message message) throws IOException {
        byte[] data = serialize(message);
        InetAddress group = InetAddress.getByName(MESH_GROUP);
        DatagramPacket packet = new DatagramPacket(data, data.length, group, MESH_PORT);
        socket.send(packet);
    }

    public void stop() {
        running = false;

        if (discoveryThread != null) {
            discoveryThread.interrupt();
        }
        if (relayThread != null) {
            relayThread.interrupt();
        }

        close();

        joinQuietly(discoveryThread);
        joinQuietly(relayThread);
        discoveryThread = null;
        relayThread = null;
    }

    private void joinQuietly(Thread thread) {
        if (thread == null) {
            return;
        }
        try {
            thread.join(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected synchronized void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    protected abstract void handleMessage(Message message);


    private static NetworkInterface findMulticastInterface() throws SocketException {
        for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (!ni.isUp() || ni.isLoopback() || !ni.supportsMulticast()) {
                continue;
            }
            boolean hasIpv4 = Collections.list(ni.getInetAddresses()).stream()
                    .anyMatch(address -> address instanceof java.net.Inet4Address);
            if (!hasIpv4) {
                continue;
            }
            try (MulticastSocket probe = new MulticastSocket(0)) {
                probe.setNetworkInterface(ni);
                return ni;
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    protected static byte[] serialize(Message message) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectStream = new ObjectOutputStream(byteStream)) {
            objectStream.writeObject(message);
        }
        return byteStream.toByteArray();
    }

    protected static Message deserialize(byte[] data, int length) {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(data, 0, length);
        try (ObjectInputStream objectStream = new ObjectInputStream(byteStream)) {
            return (Message) objectStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    protected void log(String text) {
        System.out.println("[" + nodeId + "] " + text);
    }


    public String getNodeId() {
        return nodeId;
    }

    public String getRole() {
        return role;
    }

    public Set<String> getNeighbors() {
        return neighbors;
    }

    public Queue<Message> getMessageQueue() {
        return messageQueue;
    }

    public int getDuplicatesSuppressed() {
        return duplicatesSuppressed.get();
    }
}
