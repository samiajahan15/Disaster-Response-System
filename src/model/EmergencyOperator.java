package model;

import interfaces.Reportable;


public class EmergencyOperator extends User implements Reportable {
    private static final long serialVersionUID = 1L;

    private int emergenciesReported;

    public EmergencyOperator(String userId, String name, String username, String password) {
        super(userId, name, username, password, "EMERGENCY_OPERATOR");
        this.emergenciesReported = 0;
    }


    @Override
    public String getDisplayInfo() {
        return "Emergency Operator: " + getName()
             + " | Emergencies Reported: " + emergenciesReported;
    }


    @Override
    public String generateReport() {
        return "Operator " + getName() + " has reported "
             + emergenciesReported + " emergencies.";
    }

    public void incrementEmergenciesReported() {
        this.emergenciesReported++;
    }

    public int getEmergenciesReported() {
        return emergenciesReported;
    }
}
