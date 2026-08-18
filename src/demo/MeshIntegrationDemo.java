package demo;

import mesh.Message;
import mesh.RelayNode;
import mesh.SosMessage;
import model.AccidentEmergency;
import model.EarthquakeEmergency;
import model.Emergency;
import model.FireEmergency;
import model.FloodEmergency;
import persistence.CsvDataManager;
import protocol.ProtocolAdapter;
import protocol.Request;
import protocol.Response;
import util.Constants;
import util.InputValidator;

import java.util.List;
import java.util.Scanner;

public class MeshIntegrationDemo {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        String nodeId = args.length > 0 ? args[0] : "USER-1";
        runUserApplication(nodeId);
    }

    private static void runUserApplication(String nodeId) {
        CsvDataManager data = new CsvDataManager();
        RelayNode user = new RelayNode(nodeId, Constants.ROLE_OPERATOR);

        try {
            data.initializeDataFiles();
            user.openSocket();
            Thread networkThread = new Thread(user, nodeId + "-application");
            networkThread.start();

            System.out.println();
            System.out.println("==============================================");
            System.out.println("              DISASTERMESH");
            System.out.println("       EMERGENCY OPERATOR APPLICATION");
            System.out.println("==============================================");
            System.out.println("Node ID : " + nodeId);
            System.out.println("Status  : ONLINE");
            System.out.println("Network : UDP multicast mesh");
            System.out.println("==============================================");

            boolean running = true;
            while (running) {
                printMenu(nodeId, user);
                String choice = readChoice();

                switch (choice) {
                    case "1":
                        reportEmergency(user, data);
                        break;
                    case "2":
                        viewEmergencies(data);
                        break;
                    case "3":
                        viewNetwork(user);
                        break;
                    case "4":
                        sendChat(user);
                        break;
                    case "5":
                        viewDelivery(user);
                        break;
                    case "6":
                        System.out.println("Node " + nodeId + " is ONLINE.");
                        break;
                    case "7":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            }
        } catch (Exception e) {
            System.out.println("Application error: " + e.getMessage());
        } finally {
            user.stop();
            System.out.println("DisasterMesh operator node stopped.");
        }
    }

    private static void printMenu(String nodeId, RelayNode user) {
        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("USER NODE: " + nodeId);
        System.out.println("Neighbours: " + user.getNeighbors());
        System.out.println("Pending messages: " + user.getPendingCount());
        System.out.println("----------------------------------------------");
        System.out.println("1. Report emergency");
        System.out.println("2. View saved emergencies");
        System.out.println("3. View mesh network");
        System.out.println("4. Send mesh message");
        System.out.println("5. View delivery acknowledgements");
        System.out.println("6. Node status");
        System.out.println("7. Exit");
        System.out.print("Select option: ");
    }

    private static void reportEmergency(RelayNode user, CsvDataManager data) {
        try {
            System.out.println();
            System.out.println("========== REPORT EMERGENCY ==========");

            String victim = readRequired("Victim name");
            String location = readRequired("Location");
            String description = readRequired("Description");

            System.out.println("Emergency type:");
            System.out.println("1. FLOOD");
            System.out.println("2. FIRE");
            System.out.println("3. EARTHQUAKE");
            System.out.println("4. ACCIDENT");
            String typeChoice = readRequired("Select type");

            String type;
            switch (typeChoice) {
                case "1":
                    type = Constants.TYPE_FLOOD;
                    break;
                case "2":
                    type = Constants.TYPE_FIRE;
                    break;
                case "3":
                    type = Constants.TYPE_EARTHQUAKE;
                    break;
                case "4":
                    type = Constants.TYPE_ACCIDENT;
                    break;
                default:
                    System.out.println("Invalid emergency type.");
                    return;
            }

            int severity = readInt("Severity (1-10)", 1, 10);
            int affectedPeople = readInt("Affected people", 1, Integer.MAX_VALUE);

            double waterLevel = 0;
            double area = 0;
            String spread = "";
            boolean hazardous = false;
            double magnitude = 0;
            String risk = "";
            int vehicles = 0;
            boolean blocked = false;

            if (Constants.TYPE_FLOOD.equals(type)) {
                waterLevel = readDouble("Water level in metres");
                area = readDouble("Area affected in sq km");
            } else if (Constants.TYPE_FIRE.equals(type)) {
                spread = readRequired("Fire spread rate (LOW/MEDIUM/HIGH)");
                hazardous = readYesNo("Hazardous materials present");
            } else if (Constants.TYPE_EARTHQUAKE.equals(type)) {
                magnitude = readDouble("Magnitude");
                risk = readRequired("Aftershock risk (LOW/MEDIUM/HIGH)");
            } else {
                vehicles = readInt("Vehicles involved", 1, Integer.MAX_VALUE);
                blocked = readYesNo("Road blocked");
            }

            Request request = ProtocolAdapter.emergencyReport(
                    user.getNodeId(), victim, location, type, severity, 3);
            SosMessage sos = ProtocolAdapter.toSosMessage(request);

            Emergency emergency;
            if (Constants.TYPE_FLOOD.equals(type)) {
                emergency = new FloodEmergency(
                        sos.getId(), location, description, severity, affectedPeople,
                        user.getNodeId(), waterLevel, area);
            } else if (Constants.TYPE_FIRE.equals(type)) {
                emergency = new FireEmergency(
                        sos.getId(), location, description, severity, affectedPeople,
                        user.getNodeId(), spread, hazardous);
            } else if (Constants.TYPE_EARTHQUAKE.equals(type)) {
                emergency = new EarthquakeEmergency(
                        sos.getId(), location, description, severity, affectedPeople,
                        user.getNodeId(), magnitude, risk);
            } else {
                emergency = new AccidentEmergency(
                        sos.getId(), location, description, severity, affectedPeople,
                        user.getNodeId(), vehicles, blocked);
            }

            emergency.calculatePriority();

            System.out.println();
            System.out.println("---------- EMERGENCY REVIEW ----------");
            System.out.println(emergency.generateReport());
            System.out.println("SOS ID: " + sos.getId());
            System.out.println("Victim: " + victim);
            System.out.println("TTL: " + sos.getTtl() + " hops");

            if (!readYesNo("Confirm and transmit this emergency")) {
                System.out.println("Emergency cancelled.");
                return;
            }

            data.saveEmergency(emergency);
            Response response = ProtocolAdapter.accepted(sos);

            System.out.println();
            System.out.println("✓ Emergency saved to data/emergencies.csv");
            System.out.println("✓ Protocol response: " + response.getMessage());
            System.out.println("✓ SOS ID: " + sos.getId());

            user.sendSos(sos);

            System.out.println("✓ SOS broadcast to the DisasterMesh.");
            System.out.println("Waiting for acknowledgements...");
        } catch (Exception e) {
            System.out.println("Could not create emergency: " + e.getMessage());
        }
    }

    private static void viewEmergencies(CsvDataManager data) {
        try {
            List<Emergency> emergencies = data.loadEmergencies();
            System.out.println();
            System.out.println("========== SAVED EMERGENCIES ==========");
            if (emergencies.isEmpty()) {
                System.out.println("No emergencies found.");
                return;
            }
            for (Emergency emergency : emergencies) {
                System.out.println(emergency);
            }
        } catch (Exception e) {
            System.out.println("Could not load emergencies: " + e.getMessage());
        }
    }

    private static void viewNetwork(RelayNode user) {
        System.out.println();
        System.out.println("========== MESH NETWORK ==========");
        System.out.println("Node: " + user.getNodeId());
        System.out.println("Role: " + user.getRole());
        System.out.println("Neighbours: " + user.getNeighbors());
        System.out.println("Messages waiting: " + user.getPendingCount());
        System.out.println("Messages suppressed as duplicates: " + user.getDuplicatesSuppressed());
    }

    private static void sendChat(RelayNode user) {
        String recipient = readRequired("Recipient node ID or ALL");
        String message = readRequired("Message");
        try {
            user.sendChat(recipient, message);
            System.out.println("Message broadcast through the mesh.");
        } catch (Exception e) {
            System.out.println("Message could not be sent: " + e.getMessage());
        }
    }

    private static void viewDelivery(RelayNode user) {
        System.out.println();
        System.out.println("========== DELIVERY STATUS ==========");
        List<String> acknowledgements = user.getAcknowledgements();
        if (acknowledgements.isEmpty()) {
            System.out.println("No acknowledgements received yet.");
        } else {
            for (String acknowledgement : acknowledgements) {
                System.out.println("✓ " + acknowledgement);
            }
        }
    }

    private static String readRequired(String label) {
        while (true) {
            System.out.print(label + ": ");
            String value = SCANNER.nextLine().trim();
            if (!InputValidator.isNullOrEmpty(value)) {
                return value;
            }
            System.out.println("This field is required.");
        }
    }

    private static int readInt(String label, int min, int max) {
        while (true) {
            System.out.print(label + ": ");
            String value = SCANNER.nextLine().trim();
            try {
                int number = Integer.parseInt(value);
                if (number >= min && number <= max) {
                    return number;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Enter a valid number.");
        }
    }

    private static double readDouble(String label) {
        while (true) {
            System.out.print(label + ": ");
            String value = SCANNER.nextLine().trim();
            try {
                double number = Double.parseDouble(value);
                if (number > 0) {
                    return number;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Enter a positive number.");
        }
    }

    private static boolean readYesNo(String label) {
        while (true) {
            System.out.print(label + " [Y/N]: ");
            String value = SCANNER.nextLine().trim().toUpperCase();
            if ("Y".equals(value)) {
                return true;
            }
            if ("N".equals(value)) {
                return false;
            }
            System.out.println("Enter Y or N.");
        }
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
