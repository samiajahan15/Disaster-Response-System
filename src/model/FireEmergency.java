package model;


public class FireEmergency extends Emergency {
    private static final long serialVersionUID = 1L;

    private String fireSpreadRate;
    private boolean hazardousMaterials;


    public FireEmergency(String emergencyId, String location, String description,
                         int severity, int affectedPeople, String reportedBy,
                         String fireSpreadRate, boolean hazardousMaterials) {
        super(emergencyId, location, description, severity, affectedPeople, reportedBy);
        this.fireSpreadRate = fireSpreadRate;
        this.hazardousMaterials = hazardousMaterials;
    }


    public FireEmergency(String emergencyId, String location, String description,
                         int severity, int affectedPeople, String timestamp,
                         String status, String priority, String reportedBy,
                         String fireSpreadRate, boolean hazardousMaterials) {
        super(emergencyId, location, description, severity, affectedPeople,
              timestamp, status, priority, reportedBy);
        this.fireSpreadRate = fireSpreadRate;
        this.hazardousMaterials = hazardousMaterials;
    }


    @Override
    public String calculatePriority() {
        int score = calculateBaseScore();

        if ("HIGH".equalsIgnoreCase(fireSpreadRate)) {
            score += 20;
        }
        if (hazardousMaterials) {
            score += 25;
        }

        String priority = scoreToPriority(score);
        setPriority(priority);
        return priority;
    }

    @Override
    public String getEmergencyType() {
        return "FIRE";
    }

    @Override
    public String getSpecificDetails() {
        return String.format("Fire Spread Rate: %s | Hazardous Materials: %s",
            fireSpreadRate, hazardousMaterials ? "Yes" : "No");
    }

    public String getFireSpreadRate() {
        return fireSpreadRate;
    }

    public boolean hasHazardousMaterials() {
        return hazardousMaterials;
    }
}
