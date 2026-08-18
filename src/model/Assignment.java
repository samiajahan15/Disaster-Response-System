package model;

import interfaces.StatusUpdatable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Assignment implements Serializable, StatusUpdatable {
    private static final long serialVersionUID = 1L;

    private String assignmentId;
    private String emergencyId;
    private String teamId;
    private String status;
    private String assignedAt;
    private String completedAt;


    public Assignment(String assignmentId, String emergencyId, String teamId) {
        this.assignmentId = assignmentId;
        this.emergencyId = emergencyId;
        this.teamId = teamId;
        this.status = "PENDING";
        this.assignedAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.completedAt = "";
    }


    public Assignment(String assignmentId, String emergencyId, String teamId,
                      String status, String assignedAt, String completedAt) {
        this.assignmentId = assignmentId;
        this.emergencyId = emergencyId;
        this.teamId = teamId;
        this.status = status;
        this.assignedAt = assignedAt;
        this.completedAt = (completedAt == null) ? "" : completedAt;
    }


    public void markCompleted() {
        this.status = "COMPLETED";
        this.completedAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    @Override
    public void updateStatus(String newStatus) {
        this.status = newStatus;
        if ("COMPLETED".equals(newStatus)) {
            this.completedAt = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    @Override
    public String getStatus() {
        return status;
    }



    public String getAssignmentId() {
        return assignmentId;
    }

    public String getEmergencyId() {
        return emergencyId;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getAssignedAt() {
        return assignedAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    @Override
    public String toString() {
        return String.format("[%s] Emergency: %s -> Team: %s | Status: %s | Assigned: %s",
            assignmentId, emergencyId, teamId, status, assignedAt);
    }
}
