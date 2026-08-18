package util;


public class Constants {

    public static final int SERVER_PORT = 5050;
    public static final String SERVER_HOST = "localhost";

    public static final String DATA_DIR = "data";
    public static final String USERS_FILE = DATA_DIR + "/users.csv";
    public static final String EMERGENCIES_FILE = DATA_DIR + "/emergencies.csv";
    public static final String RESCUE_TEAMS_FILE = DATA_DIR + "/rescue_teams.csv";
    public static final String HOSPITALS_FILE = DATA_DIR + "/hospitals.csv";
    public static final String ASSIGNMENTS_FILE = DATA_DIR + "/assignments.csv";

    public static final String ROLE_OPERATOR = "EMERGENCY_OPERATOR";
    public static final String ROLE_RESCUE = "RESCUE_OFFICER";
    public static final String ROLE_HOSPITAL = "HOSPITAL_LIAISON";
    public static final String ROLE_MONITOR = "OPERATIONS_MONITOR";

    public static final String STATUS_REPORTED = "REPORTED";
    public static final String STATUS_ACKNOWLEDGED = "ACKNOWLEDGED";
    public static final String STATUS_ASSIGNED = "ASSIGNED";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_RESOLVED = "RESOLVED";
    public static final String STATUS_CLOSED = "CLOSED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    public static final String TEAM_AVAILABLE = "AVAILABLE";
    public static final String TEAM_ASSIGNED = "ASSIGNED";
    public static final String TEAM_EN_ROUTE = "EN_ROUTE";
    public static final String TEAM_ON_SCENE = "ON_SCENE";
    public static final String TEAM_COMPLETED = "COMPLETED";
    public static final String TEAM_UNAVAILABLE = "UNAVAILABLE";

    public static final String ASSIGN_PENDING = "PENDING";
    public static final String ASSIGN_ACCEPTED = "ACCEPTED";
    public static final String ASSIGN_REJECTED = "REJECTED";
    public static final String ASSIGN_IN_PROGRESS = "IN_PROGRESS";
    public static final String ASSIGN_COMPLETED = "COMPLETED";

    public static final String PRIORITY_CRITICAL = "CRITICAL";
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_MEDIUM = "MEDIUM";
    public static final String PRIORITY_LOW = "LOW";

    public static final String TYPE_FLOOD = "FLOOD";
    public static final String TYPE_FIRE = "FIRE";
    public static final String TYPE_EARTHQUAKE = "EARTHQUAKE";
    public static final String TYPE_ACCIDENT = "ACCIDENT";

    private Constants() {
    }
}
