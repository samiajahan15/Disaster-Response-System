package model;


public class FloodEmergency extends Emergency {
    private static final long serialVersionUID = 1L;

    private double waterLevel;
    private double areaAffected;

    public FloodEmergency(String emergencyId, String location, String description,
                          int severity, int affectedPeople, String reportedBy,
                          double waterLevel, double areaAffected) {
        super(emergencyId, location, description, severity, affectedPeople, reportedBy);
        this.waterLevel = waterLevel;
        this.areaAffected = areaAffected;
    }


    public FloodEmergency(String emergencyId, String location, String description,
                          int severity, int affectedPeople, String timestamp,
                          String status, String priority, String reportedBy,
                          double waterLevel, double areaAffected) {
        super(emergencyId, location, description, severity, affectedPeople,
              timestamp, status, priority, reportedBy);
        this.waterLevel = waterLevel;
        this.areaAffected = areaAffected;
    }


    @Override
    public String calculatePriority() {
        int score = calculateBaseScore();

        if (waterLevel >= 3.0) {
            score += 20;
        }
        if (areaAffected >= 10.0) {
            score += 15;
        }

        String priority = scoreToPriority(score);
        setPriority(priority);
        return priority;
    }

    @Override
    public String getEmergencyType() {
        return "FLOOD";
    }

    @Override
    public String getSpecificDetails() {
        return String.format("Water Level: %.1f m | Area Affected: %.1f sq km",
            waterLevel, areaAffected);
    }

    public double getWaterLevel() {
        return waterLevel;
    }

    public double getAreaAffected() {
        return areaAffected;
    }
}
