package protocol;

import mesh.SosMessage;





public final class ProtocolAdapter {

    private ProtocolAdapter() {
    }

    public static Request emergencyReport(String username,
                                          String victim,
                                          String location,
                                          String emergencyType,
                                          int severity,
                                          int ttl) {
        return new Request(CommandType.REPORT_EMERGENCY, username)
                .put("victim", victim)
                .put("location", location)
                .put("emergencyType", emergencyType)
                .put("severity", String.valueOf(severity))
                .put("ttl", String.valueOf(ttl));
    }

    public static SosMessage toSosMessage(Request request) {
        if (request == null || request.getCommand() != CommandType.REPORT_EMERGENCY) {
            throw new IllegalArgumentException("Request must be REPORT_EMERGENCY");
        }

        int severity = parseInt(request.get("severity"), "severity");
        int ttl = parseInt(request.get("ttl"), "ttl");

        return new SosMessage(
                request.getUsername(),
                request.get("victim"),
                request.get("location"),
                request.get("emergencyType"),
                severity,
                ttl);
    }

    public static Response accepted(SosMessage sos) {
        return Response.ok("Emergency report accepted")
                .put("messageId", sos.getId())
                .put("type", sos.getEmergencyType())
                .put("severity", String.valueOf(sos.getSeverity()));
    }

    private static int parseInt(String value, String field) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + field + ": " + value, e);
        }
    }
}
