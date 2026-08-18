package model;

import interfaces.Notifiable;


public class OperationsMonitor extends User implements Notifiable {
    private static final long serialVersionUID = 1L;

    public OperationsMonitor(String userId, String name, String username, String password) {
        super(userId, name, username, password, "OPERATIONS_MONITOR");
    }

    @Override
    public String getDisplayInfo() {
        return "Operations Monitor: " + getName();
    }


    @Override
    public String receiveNotification(String message) {
        return "[MONITOR UPDATE] " + message;
    }
}
