package model;

import interfaces.Reportable;
import interfaces.StatusUpdatable;
import java.io.Serializable;


public class Hospital implements Serializable, Reportable, StatusUpdatable {
    private static final long serialVersionUID = 1L;

    private String hospitalId;
    private String hospitalName;
    private int availableBeds;
    private int emergencyBeds;
    private int medicalStaff;
    private String resources;
    private String status;
    private String linkedUserId;

    public Hospital(String hospitalId, String hospitalName, int availableBeds,
                    int emergencyBeds, int medicalStaff, String resources,
                    String linkedUserId) {
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.availableBeds = availableBeds;
        this.emergencyBeds = emergencyBeds;
        this.medicalStaff = medicalStaff;
        this.resources = resources;
        this.status = "OPERATIONAL";
        this.linkedUserId = linkedUserId;
    }


    public Hospital(String hospitalId, String hospitalName, int availableBeds,
                    int emergencyBeds, int medicalStaff, String resources,
                    String status, String linkedUserId) {
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.availableBeds = availableBeds;
        this.emergencyBeds = emergencyBeds;
        this.medicalStaff = medicalStaff;
        this.resources = resources;
        this.status = status;
        this.linkedUserId = linkedUserId;
    }


    @Override
    public String generateReport() {
        return String.format(
            "Hospital Report [%s]%n"
          + "Name: %s | Status: %s%n"
          + "Available Beds: %d | Emergency Beds: %d%n"
          + "Medical Staff: %d%n"
          + "Resources: %s",
            hospitalId, hospitalName, status,
            availableBeds, emergencyBeds, medicalStaff, resources
        );
    }


    @Override
    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    @Override
    public String getStatus() {
        return status;
    }


    public String getHospitalId() {
        return hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public int getAvailableBeds() {
        return availableBeds;
    }

    public void setAvailableBeds(int availableBeds) {
        this.availableBeds = availableBeds;
    }

    public int getEmergencyBeds() {
        return emergencyBeds;
    }

    public void setEmergencyBeds(int emergencyBeds) {
        this.emergencyBeds = emergencyBeds;
    }

    public int getMedicalStaff() {
        return medicalStaff;
    }

    public void setMedicalStaff(int medicalStaff) {
        this.medicalStaff = medicalStaff;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public String getLinkedUserId() {
        return linkedUserId;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Beds: %d | Emergency: %d | Staff: %d | %s",
            hospitalId, hospitalName, availableBeds, emergencyBeds, medicalStaff, status);
    }
}
