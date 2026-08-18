package interfaces;

public interface Assignable {

    void assignToEmergency(String emergencyId);

    void unassign();

    boolean isAvailable();
}
