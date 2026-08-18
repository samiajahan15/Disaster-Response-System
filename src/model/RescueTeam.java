package model;

import interfaces.Assignable;
import interfaces.StatusUpdatable;
import java.io.Serializable;


public class RescueTeam implements Serializable, Assignable, StatusUpdatable {
    private static final long serialVersionUID = 1L;

    private String teamId;
    private String teamName;
    private String specialization;
    private int memberCount;
    private String status;
    private String currentAssignmentId;
    private String linkedUserId;


    public RescueTeam(String teamId, String teamName, String specialization,
                      int memberCount, String linkedUserId) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.specialization = specialization;
        this.memberCount = memberCount;
        this.status = "AVAILABLE";
        this.currentAssignmentId = "";
        this.linkedUserId = linkedUserId;
    }


    public RescueTeam(String teamId, String teamName, String specialization,
                      int memberCount, String status, String currentAssignmentId,
                      String linkedUserId) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.specialization = specialization;
        this.memberCount = memberCount;
        this.status = status;
        this.currentAssignmentId = (currentAssignmentId == null) ? "" : currentAssignmentId;
        this.linkedUserId = linkedUserId;
    }


    @Override
    public void assignToEmergency(String emergencyId) {
        this.currentAssignmentId = emergencyId;
        this.status = "ASSIGNED";
    }

    @Override
    public void unassign() {
        this.currentAssignmentId = "";
        this.status = "AVAILABLE";
    }

    @Override
    public boolean isAvailable() {
        return "AVAILABLE".equals(this.status);
    }



    @Override
    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    @Override
    public String getStatus() {
        return status;
    }



    public String getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public String getCurrentAssignmentId() {
        return currentAssignmentId;
    }

    public void setCurrentAssignmentId(String currentAssignmentId) {
        this.currentAssignmentId = currentAssignmentId;
    }

    public String getLinkedUserId() {
        return linkedUserId;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Specialization: %s | Members: %d | Status: %s",
            teamId, teamName, specialization, memberCount, status);
    }
}
