package model;

import interfaces.Reportable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public abstract class Emergency implements Serializable, Reportable {
    private static final long serialVersionUID = 1L;

    private String emergencyId;
    private String location;
    private String description;
    private int severity;
    private int affectedPeople;
    private String timestamp;
    private String status;
    private String priority;
    private String reportedBy;
    public Emergency(String emergencyId, String location, String description,
                     int severity, int affectedPeople, String reportedBy) {
        this.emergencyId = emergencyId;
        this.location = location;
        this.description = description;
        this.severity = severity;
        this.affectedPeople = affectedPeople;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.status = "REPORTED";
        this.priority = "PENDING";
        this.reportedBy = reportedBy;
    }


    public Emergency(String emergencyId, String location, String description,
                     int severity, int affectedPeople, String timestamp,
                     String status, String priority, String reportedBy) {
        this.emergencyId = emergencyId;
        this.location = location;
        this.description = description;
        this.severity = severity;
        this.affectedPeople = affectedPeople;
        this.timestamp = timestamp;
        this.status = status;
        this.priority = priority;
        this.reportedBy = reportedBy;
    }

    public abstract String calculatePriority();

    public abstract String getEmergencyType();


    public abstract String getSpecificDetails();


    protected int calculateBaseScore() {
        int score = severity * 10;

        if (affectedPeople >= 100) {
            score += 40;
        } else if (affectedPeople >= 50) {
            score += 30;
        } else if (affectedPeople >= 20) {
            score += 20;
        } else if (affectedPeople >= 5) {
            score += 10;
        } else {
            score += 5;
        }

        return score;
    }


    protected String scoreToPriority(int score) {
        if (score >= 80) return "CRITICAL";
        if (score >= 60) return "HIGH";
        if (score >= 40) return "MEDIUM";
        return "LOW";
    }


    @Override
    public String generateReport() {
        return String.format(
            "Emergency Report [%s]%n"
          + "Type: %s | Priority: %s | Status: %s%n"
          + "Location: %s%n"
          + "Description: %s%n"
          + "Severity: %d/10 | Affected People: %d%n"
          + "Reported: %s | By: %s%n"
          + "%s",
            emergencyId, getEmergencyType(), priority, status,
            location, description, severity, affectedPeople,
            timestamp, reportedBy, getSpecificDetails()
        );
    }



    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public int getSeverity() {
        return severity;
    }

    public int getAffectedPeople() {
        return affectedPeople;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s | %s | %s | Affected: %d",
            emergencyId, getEmergencyType(), location, priority, status, affectedPeople);
    }
}
