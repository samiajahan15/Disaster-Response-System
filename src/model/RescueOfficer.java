package model;


public class RescueOfficer extends User {
    private static final long serialVersionUID = 1L;

    private String teamId;


    public RescueOfficer(String userId, String name, String username, String password) {
        super(userId, name, username, password, "RESCUE_OFFICER");
        this.teamId = "";
    }

    public RescueOfficer(String userId, String name, String username, String password,
                         String teamId) {
        super(userId, name, username, password, "RESCUE_OFFICER");
        this.teamId = teamId;
    }

    @Override
    public String getDisplayInfo() {
        return "Rescue Officer: " + getName()
             + " | Team: " + (teamId.isEmpty() ? "Unassigned" : teamId);
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }
}
