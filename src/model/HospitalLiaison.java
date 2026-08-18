package model;

import interfaces.Notifiable;


public class HospitalLiaison extends User implements Notifiable {
    private static final long serialVersionUID = 1L;

    private String hospitalId;

    public HospitalLiaison(String userId, String name, String username, String password) {
        super(userId, name, username, password, "HOSPITAL_LIAISON");
        this.hospitalId = "";
    }


    public HospitalLiaison(String userId, String name, String username, String password,
                           String hospitalId) {
        super(userId, name, username, password, "HOSPITAL_LIAISON");
        this.hospitalId = hospitalId;
    }

    @Override
    public String getDisplayInfo() {
        return "Hospital Liaison: " + getName()
             + " | Hospital: " + (hospitalId.isEmpty() ? "Unassigned" : hospitalId);
    }


    @Override
    public String receiveNotification(String message) {
        return "[HOSPITAL ALERT] " + getName() + ": " + message;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }
}
