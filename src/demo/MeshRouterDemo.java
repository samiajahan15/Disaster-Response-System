package demo;

import mesh.Message;
import mesh.RelayNode;
import util.Constants;

import java.util.Scanner;

public class MeshRouterDemo {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        String nodeId = args.length > 0 ? args[0] : "RELAY-1";
        RelayNode relay = new RelayNode(nodeId, RelayNode.ROLE_RELAY);

        try {
            relay.openSocket();
            Thread networkThread = new Thread(relay, nodeId + "-application");
            networkThread.start();

            System.out.println();
            System.out.println("==============================================");
            System.out.println("              DISASTERMESH");
            System.out.println("              RELAY NODE");
            System.out.println("==============================================");
            System.out.println("Node ID : " + nodeId);
            System.out.println("Status  : ONLINE");
            System.out.println("Role    : MESH RELAY");
            System.out.println("==============================================");

            boolean running = true;
            while (running) {
                System.out.println();
                System.out.println("----------------------------------------------");
                System.out.println("RELAY NODE: " + relay.getNodeId());
                System.out.println("Neighbours: " + relay.getNeighbors());
                System.out.println("Carried messages: " + relay.getMessageQueue().size());
                System.out.println("----------------------------------------------");
                System.out.println("1. View carried messages");
                System.out.println("2. Send mesh message");
                System.out.println("3. View network");
                System.out.println("4. View routing statistics");
                System.out.println("5. Exit");
                System.out.print("Select option: ");

                String choice = readChoice();
                switch (choice) {
                    case "1":
                        viewMessages(relay);
                        break;
                    case "2":
                        sendChat(relay);
                        break;
                    case "3":
                        viewNetwork(relay);
                        break;
                    case "4":
                        viewStats(relay);
                        break;
                    case "5":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            }
        } catch (Exception e) {
            System.out.println("Relay application error: " + e.getMessage());
        } finally {
            relay.stop();
            System.out.println("Relay node stopped.");
        }
    }

    private static void viewMessages(RelayNode relay) {
        System.out.println();
        System.out.println("========== CARRIED MESSAGES ==========");
        if (relay.getMessageQueue().isEmpty()) {
            System.out.println("No messages are currently carried.");
            return;
        }
        for (Message message : relay.getMessageQueue()) {
            System.out.println(message);
        }
    }

    private static void sendChat(RelayNode relay) {
        System.out.print("Recipient node ID or ALL: ");
        String recipient = SCANNER.nextLine().trim();
        System.out.print("Message: ");
        String text = SCANNER.nextLine().trim();
        if (recipient.isEmpty() || text.isEmpty()) {
            System.out.println("Recipient and message are required.");
            return;
        }

        try {
            relay.sendChat(recipient, text);
            System.out.println("Message sent through relay.");
        } catch (Exception e) {
            System.out.println("Could not send message: " + e.getMessage());
        }
    }

    private static void viewNetwork(RelayNode relay) {
        System.out.println("Node: " + relay.getNodeId());
        System.out.println("Role: " + relay.getRole());
        System.out.println("Neighbours: " + relay.getNeighbors());
    }

    private static void viewStats(RelayNode relay) {
        System.out.println("Messages carried: " + relay.getMessageQueue().size());
        System.out.println("Duplicates suppressed: " + relay.getDuplicatesSuppressed());
        System.out.println("Pending outbox: " + relay.getPendingCount());
        System.out.println("Forwarding enabled: " + relay.isForwardingEnabled());
    }

    private static String readChoice() {
        while (true) {
            String line = SCANNER.nextLine().trim();
            if (!line.isEmpty()) {
                return line.split("\\s+")[0];
            }
        }
    }

}
