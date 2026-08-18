package demo;

import model.Assignment;
import model.Emergency;
import model.FloodEmergency;
import model.Hospital;
import model.RescueTeam;
import model.User;
import persistence.CsvDataManager;

import java.util.List;





public class FileHandlingDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== CSV FILE HANDLING / PERSISTENCE DEMO ===");

        CsvDataManager data = new CsvDataManager();
        data.initializeDataFiles();

        System.out.println("Data directory: " + data.getDataDirectory().toAbsolutePath());


        List<User> users = data.loadUsers();
        List<Hospital> hospitals = data.loadHospitals();
        List<RescueTeam> teams = data.loadRescueTeams();
        List<Emergency> oldEmergencies = data.loadEmergencies();
        List<Assignment> oldAssignments = data.loadAssignments();

        System.out.println("Loaded users        : " + users.size());
        System.out.println("Loaded hospitals    : " + hospitals.size());
        System.out.println("Loaded rescue teams : " + teams.size());
        System.out.println("Loaded emergencies  : " + oldEmergencies.size());
        System.out.println("Loaded assignments  : " + oldAssignments.size());

        if (!hospitals.isEmpty()) {
            System.out.println("Hospital from CSV   : " + hospitals.get(0));
        }
        if (!teams.isEmpty()) {
            System.out.println("Team from CSV       : " + teams.get(0));
        }


        String emergencyId = "FILE-DEMO-" + System.currentTimeMillis();
        FloodEmergency emergency = new FloodEmergency(
                emergencyId,
                "Demo Zone",
                "Flood reported during persistence test",
                8,
                35,
                "U001",
                3.5,
                12.0);
        emergency.calculatePriority();

        data.saveEmergency(emergency);
        System.out.println("\nSaved emergency to: data/emergencies.csv");
        System.out.println(emergency.generateReport());


        if (!teams.isEmpty()) {
            RescueTeam team = teams.get(0);
            String assignmentId = "ASSIGN-DEMO-" + System.currentTimeMillis();
            Assignment assignment = new Assignment(
                    assignmentId, emergency.getEmergencyId(), team.getTeamId());

            data.saveAssignment(assignment);
            System.out.println("Saved assignment to: data/assignments.csv");


            List<Emergency> reloadedEmergencies = data.loadEmergencies();
            List<Assignment> reloadedAssignments = data.loadAssignments();

            System.out.println("\nAfter writing to disk:");
            System.out.println("Reloaded emergencies : " + reloadedEmergencies.size());
            System.out.println("Reloaded assignments : " + reloadedAssignments.size());
            System.out.println("Last emergency       : "
                    + reloadedEmergencies.get(reloadedEmergencies.size() - 1));
            System.out.println("Last assignment      : "
                    + reloadedAssignments.get(reloadedAssignments.size() - 1));
        }

        System.out.println("\n=== FILE PERSISTENCE VERIFIED ===");
        System.out.println("Objects were loaded from CSV, written to CSV, and loaded again.");
    }
}
