package persistence;

import model.*;
import util.Constants;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;







public class CsvDataManager {

    private final Path dataDirectory;

    public CsvDataManager() {
        this(Paths.get(Constants.DATA_DIR));
    }

    public CsvDataManager(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void initializeDataFiles() throws IOException {
        Files.createDirectories(dataDirectory);
        ensureFile(Constants.USERS_FILE, "userId,name,username,password,role");
        ensureFile(Constants.HOSPITALS_FILE,
                "hospitalId,hospitalName,availableBeds,emergencyBeds,medicalStaff,resources,status,linkedUserId");
        ensureFile(Constants.RESCUE_TEAMS_FILE,
                "teamId,teamName,specialization,memberCount,status,currentAssignmentId,linkedUserId");
        ensureFile(Constants.EMERGENCIES_FILE,
                "emergencyId,type,location,description,severity,affectedPeople,timestamp,status,priority,reportedBy,details");
        ensureFile(Constants.ASSIGNMENTS_FILE,
                "assignmentId,emergencyId,teamId,status,assignedAt,completedAt");
    }

    private void ensureFile(String configuredPath, String header) throws IOException {
        Path path = resolve(configuredPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.writeString(path, header + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        }
    }

    public List<User> loadUsers() throws IOException {
        List<User> users = new ArrayList<>();
        for (String[] row : readRows(Constants.USERS_FILE)) {
            if (row.length < 5) continue;
            String role = row[4];
            User user;
            switch (role) {
                case Constants.ROLE_OPERATOR:
                    user = new EmergencyOperator(row[0], row[1], row[2], row[3]);
                    break;
                case Constants.ROLE_RESCUE:
                    user = new RescueOfficer(row[0], row[1], row[2], row[3]);
                    break;
                case Constants.ROLE_HOSPITAL:
                    user = new HospitalLiaison(row[0], row[1], row[2], row[3]);
                    break;
                case Constants.ROLE_MONITOR:
                    user = new OperationsMonitor(row[0], row[1], row[2], row[3]);
                    break;
                default:
                    continue;
            }
            users.add(user);
        }
        return users;
    }

    public void saveUser(User user) throws IOException {
        Objects.requireNonNull(user, "user");
        initializeDataFiles();
        append(Constants.USERS_FILE,
                row(user.getUserId(), user.getName(), user.getUsername(),
                        user.getPassword(), user.getRole()));
    }

    public List<Hospital> loadHospitals() throws IOException {
        List<Hospital> hospitals = new ArrayList<>();
        for (String[] row : readRows(Constants.HOSPITALS_FILE)) {
            if (row.length < 8) continue;
            hospitals.add(new Hospital(
                    row[0], row[1], Integer.parseInt(row[2]), Integer.parseInt(row[3]),
                    Integer.parseInt(row[4]), row[5], row[6], row[7]));
        }
        return hospitals;
    }

    public void saveHospital(Hospital hospital) throws IOException {
        Objects.requireNonNull(hospital, "hospital");
        initializeDataFiles();
        append(Constants.HOSPITALS_FILE,
                row(hospital.getHospitalId(), hospital.getHospitalName(),
                        hospital.getAvailableBeds(), hospital.getEmergencyBeds(),
                        hospital.getMedicalStaff(), hospital.getResources(),
                        hospital.getStatus(), hospital.getLinkedUserId()));
    }

    public List<RescueTeam> loadRescueTeams() throws IOException {
        List<RescueTeam> teams = new ArrayList<>();
        for (String[] row : readRows(Constants.RESCUE_TEAMS_FILE)) {
            if (row.length < 7) continue;
            teams.add(new RescueTeam(
                    row[0], row[1], row[2], Integer.parseInt(row[3]),
                    row[4], row[5], row[6]));
        }
        return teams;
    }

    public void saveRescueTeam(RescueTeam team) throws IOException {
        Objects.requireNonNull(team, "team");
        initializeDataFiles();
        append(Constants.RESCUE_TEAMS_FILE,
                row(team.getTeamId(), team.getTeamName(), team.getSpecialization(),
                        team.getMemberCount(), team.getStatus(),
                        team.getCurrentAssignmentId(), team.getLinkedUserId()));
    }


    public void saveEmergency(Emergency emergency) throws IOException {
        Objects.requireNonNull(emergency, "emergency");
        initializeDataFiles();
        append(Constants.EMERGENCIES_FILE,
                row(emergency.getEmergencyId(), emergency.getEmergencyType(),
                        emergency.getLocation(), emergency.getDescription(),
                        emergency.getSeverity(), emergency.getAffectedPeople(),
                        emergency.getTimestamp(), emergency.getStatus(),
                        emergency.getPriority(), emergency.getReportedBy(),
                        emergency.getSpecificDetails()));
    }

    public List<Emergency> loadEmergencies() throws IOException {
        List<Emergency> emergencies = new ArrayList<>();
        for (String[] row : readRows(Constants.EMERGENCIES_FILE)) {
            if (row.length < 11) continue;

            String id = row[0];
            String type = row[1];
            String location = row[2];
            String description = row[3];
            int severity = Integer.parseInt(row[4]);
            int affected = Integer.parseInt(row[5]);
            String timestamp = row[6];
            String status = row[7];
            String priority = row[8];
            String reportedBy = row[9];
            String details = row[10];

            Emergency emergency = null;
            switch (type) {
                case Constants.TYPE_FLOOD:
                    double waterLevel = extractDouble(details, "Water Level:", "m");
                    double area = extractDouble(details, "Area Affected:", "sq km");
                    emergency = new FloodEmergency(id, location, description, severity, affected,
                            timestamp, status, priority, reportedBy, waterLevel, area);
                    break;
                case Constants.TYPE_FIRE:
                    String spread = extractValue(details, "Fire Spread Rate:", "|");
                    boolean hazardous = details.toLowerCase(Locale.ROOT).contains("hazardous materials: yes");
                    emergency = new FireEmergency(id, location, description, severity, affected,
                            timestamp, status, priority, reportedBy, spread, hazardous);
                    break;
                case Constants.TYPE_EARTHQUAKE:
                    double magnitude = extractDouble(details, "Magnitude:", "|");
                    String risk = extractValue(details, "Aftershock Risk:", "|");
                    emergency = new EarthquakeEmergency(id, location, description, severity, affected,
                            timestamp, status, priority, reportedBy, magnitude, risk);
                    break;
                case Constants.TYPE_ACCIDENT:
                    int vehicles = (int) extractDouble(details, "Vehicles Involved:", "|");
                    boolean blocked = details.toLowerCase(Locale.ROOT).contains("road blocked: yes");
                    emergency = new AccidentEmergency(id, location, description, severity, affected,
                            timestamp, status, priority, reportedBy, vehicles, blocked);
                    break;
                default:
                    break;
            }

            if (emergency != null) emergencies.add(emergency);
        }
        return emergencies;
    }

    public void saveAssignment(Assignment assignment) throws IOException {
        Objects.requireNonNull(assignment, "assignment");
        initializeDataFiles();
        append(Constants.ASSIGNMENTS_FILE,
                row(assignment.getAssignmentId(), assignment.getEmergencyId(),
                        assignment.getTeamId(), assignment.getStatus(),
                        assignment.getAssignedAt(), assignment.getCompletedAt()));
    }

    public List<Assignment> loadAssignments() throws IOException {
        List<Assignment> assignments = new ArrayList<>();
        for (String[] row : readRows(Constants.ASSIGNMENTS_FILE)) {
            if (row.length < 6) continue;
            assignments.add(new Assignment(row[0], row[1], row[2], row[3], row[4], row[5]));
        }
        return assignments;
    }


    public void updateRescueTeam(RescueTeam team) throws IOException {
        Objects.requireNonNull(team, "team");
        initializeDataFiles();
        List<RescueTeam> teams = loadRescueTeams();
        Path path = resolve(Constants.RESCUE_TEAMS_FILE);
        StringBuilder content = new StringBuilder("teamId,teamName,specialization,memberCount,status,currentAssignmentId,linkedUserId")
                .append(System.lineSeparator());
        for (RescueTeam item : teams) {
            RescueTeam value = item.getTeamId().equals(team.getTeamId()) ? team : item;
            content.append(row(value.getTeamId(), value.getTeamName(), value.getSpecialization(),
                    value.getMemberCount(), value.getStatus(), value.getCurrentAssignmentId(),
                    value.getLinkedUserId())).append(System.lineSeparator());
        }
        Files.writeString(path, content.toString(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void updateHospital(Hospital hospital) throws IOException {
        Objects.requireNonNull(hospital, "hospital");
        initializeDataFiles();
        List<Hospital> hospitals = loadHospitals();
        Path path = resolve(Constants.HOSPITALS_FILE);
        StringBuilder content = new StringBuilder("hospitalId,hospitalName,availableBeds,emergencyBeds,medicalStaff,resources,status,linkedUserId")
                .append(System.lineSeparator());
        for (Hospital item : hospitals) {
            Hospital value = item.getHospitalId().equals(hospital.getHospitalId()) ? hospital : item;
            content.append(row(value.getHospitalId(), value.getHospitalName(),
                    value.getAvailableBeds(), value.getEmergencyBeds(),
                    value.getMedicalStaff(), value.getResources(), value.getStatus(),
                    value.getLinkedUserId())).append(System.lineSeparator());
        }
        Files.writeString(path, content.toString(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    private List<String[]> readRows(String configuredPath) throws IOException {
        Path path = resolve(configuredPath);
        if (!Files.exists(path)) return new ArrayList<>();

        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                if (first) {
                    first = false;
                    continue;
                }
                rows.add(parseCsvLine(line));
            }
        }
        return rows;
    }

    private void append(String configuredPath, String line) throws IOException {
        Path path = resolve(configuredPath);
        Files.createDirectories(path.getParent());
        Files.writeString(path, line + System.lineSeparator(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private Path resolve(String configuredPath) {
        Path configured = Paths.get(configuredPath);
        if (configured.isAbsolute()) return configured;
        if (configured.getParent() == null) return dataDirectory.resolve(configured);
        return configured;
    }

    private static String row(Object... values) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) builder.append(',');
            builder.append(escapeCsv(String.valueOf(values[i] == null ? "" : values[i])));
        }
        return builder.toString();
    }

    private static String escapeCsv(String value) {
        if (value.indexOf(',') >= 0 || value.indexOf('"') >= 0 || value.indexOf('\n') >= 0) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (c == ',' && !quoted) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        values.add(current.toString());
        return values.toArray(new String[0]);
    }

    private static String extractValue(String details, String label, String delimiter) {
        int start = details.indexOf(label);
        if (start < 0) return "";
        start += label.length();
        int end = details.indexOf(delimiter, start);
        if (end < 0) end = details.length();
        return details.substring(start, end).trim();
    }

    private static double extractDouble(String details, String label, String unitOrDelimiter) {
        String value = extractValue(details, label, unitOrDelimiter);
        String numeric = value.replaceAll("[^0-9.+-]", "");
        if (numeric.isEmpty()) return 0.0;
        return Double.parseDouble(numeric);
    }
}
