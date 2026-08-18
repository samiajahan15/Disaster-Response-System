package model;


public class AccidentEmergency extends Emergency {
    private static final long serialVersionUID = 1L;

    private int vehiclesInvolved;
    private boolean roadBlocked;


    public AccidentEmergency(String emergencyId, String location, String description,
                             int severity, int affectedPeople, String reportedBy,
                             int vehiclesInvolved, boolean roadBlocked) {
        super(emergencyId, location, description, severity, affectedPeople, reportedBy);
        this.vehiclesInvolved = vehiclesInvolved;
        this.roadBlocked = roadBlocked;
    }


    public AccidentEmergency(String emergencyId, String location, String description,
                             int severity, int affectedPeople, String timestamp,
                             String status, String priority, String reportedBy,
                             int vehiclesInvolved, boolean roadBlocked) {
        super(emergencyId, location, description, severity, affectedPeople,
              timestamp, status, priority, reportedBy);
        this.vehiclesInvolved = vehiclesInvolved;
        this.roadBlocked = roadBlocked;
    }


    @Override
    public String calculatePriority() {
        int score = calculateBaseScore();

        if (vehiclesInvolved >= 5) {
            score += 15;
        }
        if (roadBlocked) {
            score += 10;
        }

        String priority = scoreToPriority(score);
        setPriority(priority);
        return priority;
    }

    @Override
    public String getEmergencyType() {
        return "ACCIDENT";
    }

    @Override
    public String getSpecificDetails() {
        return String.format("Vehicles Involved: %d | Road Blocked: %s",
            vehiclesInvolved, roadBlocked ? "Yes" : "No");
    }

    public int getVehiclesInvolved() {
        return vehiclesInvolved;
    }

    public boolean isRoadBlocked() {
        return roadBlocked;
    }
}
