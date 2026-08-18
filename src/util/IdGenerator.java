package util;

import java.util.concurrent.atomic.AtomicInteger;


public class IdGenerator {

    private static AtomicInteger emergencyCounter = new AtomicInteger(0);
    private static AtomicInteger assignmentCounter = new AtomicInteger(0);


    public static String generateEmergencyId() {
        return "E" + String.format("%03d", emergencyCounter.incrementAndGet());
    }


    public static String generateAssignmentId() {
        return "A" + String.format("%03d", assignmentCounter.incrementAndGet());
    }


    public static void setEmergencyCounter(int value) {
        emergencyCounter.set(value);
    }

    public static void setAssignmentCounter(int value) {
        assignmentCounter.set(value);
    }

    private IdGenerator() {
    }
}
