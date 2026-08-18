package model;


public class EarthquakeEmergency extends Emergency {
    private static final long serialVersionUID = 1L;

    private double magnitude;
    private String aftershockRisk;


    public EarthquakeEmergency(String emergencyId, String location, String description,
                               int severity, int affectedPeople, String reportedBy,
                               double magnitude, String aftershockRisk) {
        super(emergencyId, location, description, severity, affectedPeople, reportedBy);
        this.magnitude = magnitude;
        this.aftershockRisk = aftershockRisk;
    }


    public EarthquakeEmergency(String emergencyId, String location, String description,
                               int severity, int affectedPeople, String timestamp,
                               String status, String priority, String reportedBy,
                               double magnitude, String aftershockRisk) {
        super(emergencyId, location, description, severity, affectedPeople,
              timestamp, status, priority, reportedBy);
        this.magnitude = magnitude;
        this.aftershockRisk = aftershockRisk;
    }


    @Override
    public String calculatePriority() {
        int score = calculateBaseScore();

        if (magnitude >= 6.0) {
            score += 30;
        }
        if ("HIGH".equalsIgnoreCase(aftershockRisk)) {
            score += 15;
        }

        String priority = scoreToPriority(score);
        setPriority(priority);
        return priority;
    }

    @Override
    public String getEmergencyType() {
        return "EARTHQUAKE";
    }

    @Override
    public String getSpecificDetails() {
        return String.format("Magnitude: %.1f | Aftershock Risk: %s",
            magnitude, aftershockRisk);
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getAftershockRisk() {
        return aftershockRisk;
    }
}
