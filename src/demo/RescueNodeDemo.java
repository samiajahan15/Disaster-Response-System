package demo;

import mesh.Message;
import mesh.RescueNode;
import mesh.SosMessage;
import model.Assignment;
import model.Emergency;
import model.RescueTeam;
import persistence.CsvDataManager;
import util.Constants;

import java.util.List;
import java.util.Scanner;

public class RescueNodeDemo {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        String nodeId = args.length > 0 ? args[0] : "RESCUE-1";
        CsvDataManager data = new CsvDataManager();

        try {
            data.initializeDataFiles();
            List<RescueTeam> teams = data.loadRescueTeams();
            if (teams.isEmpty()) {
                System.out.println("No rescue team found in data/rescue_teams.csv.");
                return;
            }

            RescueTeam team = teams.get(0);
            RescueNode rescue = new RescueNode(nodeId, team);
            rescue.openSocket();
            Thread networkThread = new Thread(rescue, nodeId + "-application");
            networkThread.start();

            System.out.println();
            System.out.println("==============================================");
            System.out.println("              DISASTERMESH");
            System.out.println("             RESCUE NODE");
            System.out.println("==============================================");
            System.out.println("Node ID : " + nodeId);
            System.out.println("Team    : " + team.getTeamName());
            System.out.println("Status  : " + team.getStatus());
            System.out.println("==============================================");

            boolean running = true;
            while (running) {
                printMenu(rescue);
                String choice = readChoice();

                switch (choice) {
                    case "1":
                        viewAlerts(rescue);
                        break;
                    case "2":
                        dispatchTeam(rescue, data);
                        break;
                    case "3":
                        updateTeamStatus(rescue, data);
                        break;
                    case "4":
                        viewAssignments(data, team.getTeamId());
                        break;
                    case "5":
                        sendChat(rescue);
                        break;
                    case "6":
                        viewNetwork(rescue);
                        break;
                    case "7":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            }

            rescue.stop();
        } catch (Exception e) {
            System.out.println("Rescue application error: " + e.getMessage());
        }
    }

    private static void printMenu(RescueNode rescue) {
        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("RESCUE NODE: " + rescue.getNodeId());
        System.out.println("Team: " + rescue.getRescueTeam().getTeamName());
        System.out.println("Status: " + rescue.getRescueTeam().getStatus());
        System.out.println("Neighbours: " + rescue.getNeighbors());
        System.out.println("----------------------------------------------");
        System.out.println("1. View emergency alerts");
        System.out.println("2. Dispatch rescue team");
        System.out.println("3. Update team status");
        System.out.println("4. View assignments");
        System.out.println("5. Send mesh message");
        System.out.println("6. View mesh network");
        System.out.println("7. Exit");
        System.out.print("Select option: ");
    }

    private static void viewAlerts(RescueNode rescue) {
        System.out.println();
        System.out.println("========== EMERGENCY ALERTS ==========");
        boolean found = false;
        for (Message message : rescue.getMessageQueue()) {
            if (message instanceof SosMessage) {
                SosMessage sos = (SosMessage) message;
                System.out.println("SOS ID     : " + sos.getId());
                System.out.println("Victim     : " + sos.getVictimName());
                System.out.println("Location   : " + sos.getLocation());
                System.out.println("Type       : " + sos.getEmergencyType());
                System.out.println("Severity   : " + sos.getSeverity());
                System.out.println("TTL        : " + sos.getTtl());
                System.out.println("--------------------------------------");
                found = true;
            }
        }
        if (!found) {
            System.out.println("No SOS alerts received yet.");
        }
    }

    private static void dispatchTeam(RescueNode rescue, CsvDataManager data) {
        try {
            SosMessage sos = latestSos(rescue);
            if (sos == null) {
                System.out.println("No received SOS is available.");
                return;
            }

            RescueTeam team = rescue.getRescueTeam();
            if (!team.isAvailable()) {
                System.out.println("Team is currently " + team.getStatus() + ".");
                return;
            }

            System.out.println();
            System.out.println("========== RESCUE DISPATCH ==========");
            System.out.println("SOS ID   : " + sos.getId());
            System.out.println("Victim   : " + sos.getVictimName());
            System.out.println("Location : " + sos.getLocation());
            System.out.println("Severity : " + sos.getSeverity());
            System.out.println("Team     : " + team.getTeamName());

            if (!readYesNo("Dispatch this team")) {
                System.out.println("Dispatch cancelled.");
                return;
            }

            team.assignToEmergency(sos.getId());
            Assignment assignment = new Assignment(
                    "ASSIGN-" + System.currentTimeMillis(),
                    sos.getId(),
                    team.getTeamId());
            data.saveAssignment(assignment);
            data.updateRescueTeam(team);

            System.out.println("✓ Rescue team dispatched.");
            System.out.println("✓ Assignment saved to data/assignments.csv");
            System.out.println("✓ Team status: " + team.getStatus());
        } catch (Exception e) {
            System.out.println("Could not dispatch team: " + e.getMessage());
        }
    }

    private static void updateTeamStatus(RescueNode rescue, CsvDataManager data) {
        System.out.println("Current status: " + rescue.getRescueTeam().getStatus());
        System.out.println("1. AVAILABLE");
        System.out.println("2. EN_ROUTE");
        System.out.println("3. ON_SCENE");
        System.out.println("4. COMPLETED");
        System.out.print("Select status: ");
        String choice = readChoice();

        String status;
        switch (choice) {
            case "1":
                status = Constants.TEAM_AVAILABLE;
                break;
            case "2":
                status = Constants.TEAM_EN_ROUTE;
                break;
            case "3":
                status = Constants.TEAM_ON_SCENE;
                break;
            case "4":
                status = Constants.TEAM_COMPLETED;
                break;
            default:
                System.out.println("Invalid status.");
                return;
        }

        try {
            rescue.getRescueTeam().updateStatus(status);
            data.updateRescueTeam(rescue.getRescueTeam());
            System.out.println("✓ Team status updated to " + status);
        } catch (Exception e) {
            System.out.println("Could not update team status: " + e.getMessage());
        }
    }

    private static void viewAssignments(CsvDataManager data, String teamId) {
        try {
            System.out.println();
            System.out.println("========== ASSIGNMENTS ==========");
            List<Assignment> assignments = data.loadAssignments();
            boolean found = false;
            for (Assignment assignment : assignments) {
                if (teamId.equals(assignment.getTeamId())) {
                    System.out.println(assignment);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("No assignments for this team.");
            }
        } catch (Exception e) {
            System.out.println("Could not load assignments: " + e.getMessage());
        }
    }

    private static void sendChat(RescueNode rescue) {
        String recipient = readRequired("Recipient node ID or ALL");
        String message = readRequired("Message");
        try {
            rescue.sendChat(recipient, message);
            System.out.println("Message sent.");
        } catch (Exception e) {
            System.out.println("Message could not be sent: " + e.getMessage());
        }
    }

    private static void viewNetwork(RescueNode rescue) {
        System.out.println("Node: " + rescue.getNodeId());
        System.out.println("Role: " + rescue.getRole());
        System.out.println("Neighbours: " + rescue.getNeighbors());
        System.out.println("Pending: " + rescue.getPendingCount());
        System.out.println("ACKs: " + rescue.getAcknowledgements());
    }

    private static SosMessage latestSos(RescueNode rescue) {
        SosMessage latest = null;
        for (Message message : rescue.getMessageQueue()) {
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
            if (!value.isEmpty()) {
                return value;
            }
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
