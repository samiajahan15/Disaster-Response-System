package demo;

import mesh.HospitalNode;
import mesh.Message;
import mesh.SosMessage;
import model.Hospital;
import persistence.CsvDataManager;
import java.util.List;
import java.util.Scanner;

public class HospitalNodeDemo {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        String nodeId = args.length > 0 ? args[0] : "HOSPITAL-1";
        CsvDataManager data = new CsvDataManager();

        try {
            data.initializeDataFiles();
            List<Hospital> hospitals = data.loadHospitals();
            if (hospitals.isEmpty()) {
                System.out.println("No hospital found in data/hospitals.csv.");
                return;
            }

            Hospital hospital = hospitals.get(0);
            HospitalNode node = new HospitalNode(nodeId, hospital);
            node.openSocket();
            Thread networkThread = new Thread(node, nodeId + "-application");
            networkThread.start();

            System.out.println();
            System.out.println("==============================================");
            System.out.println("              DISASTERMESH");
            System.out.println("            HOSPITAL NODE");
            System.out.println("==============================================");
            System.out.println("Node ID        : " + nodeId);
            System.out.println("Hospital       : " + hospital.getHospitalName());
            System.out.println("Emergency beds : " + hospital.getEmergencyBeds());
            System.out.println("Status         : " + hospital.getStatus());
            System.out.println("==============================================");

            boolean running = true;
            while (running) {
                printMenu(node);
                String choice = readChoice();

                switch (choice) {
                    case "1":
                        viewAlerts(node);
                        break;
                    case "2":
                        prepareEmergencyBed(node, data);
                        break;
                    case "3":
                        viewHospitalStatus(node);
                        break;
                    case "4":
                        updateHospitalStatus(node, data);
                        break;
                    case "5":
                        sendChat(node);
                        break;
                    case "6":
                        viewNetwork(node);
                        break;
                    case "7":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            }

            node.stop();
        } catch (Exception e) {
            System.out.println("Hospital application error: " + e.getMessage());
        }
    }

    private static void printMenu(HospitalNode node) {
        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("HOSPITAL NODE: " + node.getNodeId());
        System.out.println("Hospital: " + node.getHospital().getHospitalName());
        System.out.println("Emergency beds: " + node.getHospital().getEmergencyBeds());
        System.out.println("Neighbours: " + node.getNeighbors());
        System.out.println("----------------------------------------------");
        System.out.println("1. View emergency alerts");
        System.out.println("2. Prepare emergency bed");
        System.out.println("3. View hospital status");
        System.out.println("4. Update hospital status");
        System.out.println("5. Send mesh message");
        System.out.println("6. View mesh network");
        System.out.println("7. Exit");
        System.out.print("Select option: ");
    }

    private static void viewAlerts(HospitalNode node) {
        System.out.println();
        System.out.println("========== MEDICAL ALERTS ==========");
        boolean found = false;
        for (Message message : node.getMessageQueue()) {
            if (message instanceof SosMessage) {
                SosMessage sos = (SosMessage) message;
                System.out.println("SOS ID     : " + sos.getId());
                System.out.println("Victim     : " + sos.getVictimName());
                System.out.println("Location   : " + sos.getLocation());
                System.out.println("Type       : " + sos.getEmergencyType());
                System.out.println("Severity   : " + sos.getSeverity());
                if (sos.getSeverity() >= 7
                        || "FIRE".equals(sos.getEmergencyType())
                        || "EARTHQUAKE".equals(sos.getEmergencyType())
                        || "ACCIDENT".equals(sos.getEmergencyType())) {
                    System.out.println("Priority   : MEDICAL RESPONSE REQUIRED");
                } else {
                    System.out.println("Priority   : ROUTINE");
                }
                System.out.println("--------------------------------------");
                found = true;
            }
        }
        if (!found) {
            System.out.println("No emergency alerts received yet.");
        }
    }

    private static void prepareEmergencyBed(HospitalNode node, CsvDataManager data) {
        Hospital hospital = node.getHospital();
        if (hospital.getEmergencyBeds() <= 0) {
            System.out.println("No emergency beds are currently available.");
            return;
        }

        SosMessage latest = latestSos(node);
        System.out.println();
        System.out.println("========== EMERGENCY PREPARATION ==========");
        if (latest != null) {
            System.out.println("Latest SOS: " + latest.getId());
            System.out.println("Victim    : " + latest.getVictimName());
            System.out.println("Severity  : " + latest.getSeverity());
            System.out.println("Location  : " + latest.getLocation());
        }

        if (!readYesNo("Prepare one emergency bed")) {
            System.out.println("Preparation cancelled.");
            return;
        }

        hospital.setEmergencyBeds(hospital.getEmergencyBeds() - 1);
        if (hospital.getAvailableBeds() > 0) {
            hospital.setAvailableBeds(hospital.getAvailableBeds() - 1);
        }

        try {
            data.updateHospital(hospital);
            System.out.println("✓ Emergency bed prepared.");
            System.out.println("✓ Hospital data updated in data/hospitals.csv");
            System.out.println("Remaining emergency beds: " + hospital.getEmergencyBeds());
        } catch (Exception e) {
            System.out.println("Could not update hospital data: " + e.getMessage());
        }
    }

    private static void viewHospitalStatus(HospitalNode node) {
        System.out.println();
        System.out.println(node.getHospital().generateReport());
    }

    private static void updateHospitalStatus(HospitalNode node, CsvDataManager data) {
        System.out.println("1. OPERATIONAL");
        System.out.println("2. BUSY");
        System.out.println("3. CLOSED");
        System.out.print("Select status: ");
        String choice = readChoice();

        String status;
        switch (choice) {
            case "1":
                status = "OPERATIONAL";
                break;
            case "2":
                status = "BUSY";
                break;
            case "3":
                status = "CLOSED";
                break;
            default:
                System.out.println("Invalid status.");
                return;
        }

        try {
            node.getHospital().updateStatus(status);
            data.updateHospital(node.getHospital());
            System.out.println("✓ Hospital status updated to " + status);
        } catch (Exception e) {
            System.out.println("Could not update hospital: " + e.getMessage());
        }
    }

    private static void sendChat(HospitalNode node) {
        String recipient = readRequired("Recipient node ID or ALL");
        String message = readRequired("Message");
        try {
            node.sendChat(recipient, message);
            System.out.println("Message sent.");
        } catch (Exception e) {
            System.out.println("Message could not be sent: " + e.getMessage());
        }
    }

    private static void viewNetwork(HospitalNode node) {
        System.out.println("Node: " + node.getNodeId());
        System.out.println("Role: " + node.getRole());
        System.out.println("Neighbours: " + node.getNeighbors());
        System.out.println("Pending: " + node.getPendingCount());
        System.out.println("ACKs: " + node.getAcknowledgements());
    }

    private static SosMessage latestSos(HospitalNode node) {
        SosMessage latest = null;
        for (Message message : node.getMessageQueue()) {
            if (message instanceof SosMessage) {
                latest = (SosMessage) message;
            }
        }
        return latest;
    }

    private static String readRequired(String label) {
        while (true) {
            System.out.print(label + ": ");
            String value = SCANNER.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("This field is required.");
        }
    }

    private static boolean readYesNo(String label) {
        while (true) {
            System.out.print(label + " [Y/N]: ");
            String value = SCANNER.nextLine().trim().toUpperCase();
            if ("Y".equals(value)) return true;
            if ("N".equals(value)) return false;
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
