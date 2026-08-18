package util;

public class InputValidator {


    public static boolean isNullOrEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }


    public static boolean isValidSeverity(int severity) {
        return severity >= 1 && severity <= 10;
    }

    public static boolean isValidSeverity(String severityStr) {
        try {
            int severity = Integer.parseInt(severityStr.trim());
            return isValidSeverity(severity);
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static boolean isPositiveInteger(String input) {
        try {
            return Integer.parseInt(input.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static boolean isNonNegativeInteger(String input) {
        try {
            return Integer.parseInt(input.trim()) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveDouble(String input) {
        try {
            return Double.parseDouble(input.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidEmergencyType(String type) {
        if (isNullOrEmpty(type)) return false;
        String upper = type.trim().toUpperCase();
        return upper.equals(Constants.TYPE_FLOOD)
            || upper.equals(Constants.TYPE_FIRE)
            || upper.equals(Constants.TYPE_EARTHQUAKE)
            || upper.equals(Constants.TYPE_ACCIDENT);
    }


    public static boolean isValidRole(String role) {
        if (isNullOrEmpty(role)) return false;
        String upper = role.trim().toUpperCase();
        return upper.equals(Constants.ROLE_OPERATOR)
            || upper.equals(Constants.ROLE_RESCUE)
            || upper.equals(Constants.ROLE_HOSPITAL)
            || upper.equals(Constants.ROLE_MONITOR);
    }


    public static boolean isValidMenuChoice(String input, int min, int max) {
        try {
            int choice = Integer.parseInt(input.trim());
            return choice >= min && choice <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private InputValidator() {
    }
}
