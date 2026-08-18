package protocol;

public enum CommandType {


    LOGIN,
    LOGOUT,
    PING,

    REPORT_EMERGENCY,
    VIEW_EMERGENCIES,

    VIEW_TEAMS,
    VIEW_HOSPITALS,
    ASSIGN_TEAM,
    UPDATE_STATUS,

    UNKNOWN;

    public static CommandType fromString(String text) {
        if (text == null) {
            return UNKNOWN;
        }
        try {
            return CommandType.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
