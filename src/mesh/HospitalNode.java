package mesh;

import model.Hospital;
import util.Constants;


public class HospitalNode extends MeshNode {


    private static final int SERIOUS_SEVERITY = 7;


    private final Hospital hospital;

    public HospitalNode(String nodeId, Hospital hospital) {
        super(nodeId, Constants.ROLE_HOSPITAL);
        this.hospital = hospital;
    }


    @Override
    protected void handleMessage(Message message) {
        super.handleMessage(message);
        if (message instanceof SosMessage) {
            SosMessage sos = (SosMessage) message;


            String report =
                  "[" + getNodeId() + "] ---- SOS ALERT FOR HOSPITAL ----\n"
                + "[" + getNodeId() + "]    Victim        : " + sos.getVictimName() + "\n"
                + "[" + getNodeId() + "]    Location      : " + sos.getLocation() + "\n"
                + "[" + getNodeId() + "]    Emergency type: " + sos.getEmergencyType() + "\n"
                + "[" + getNodeId() + "]    Severity      : " + sos.getSeverity() + "\n"
                + "[" + getNodeId() + "]    Hospital      : " + getHospitalName();
            System.out.println(report);

            if (needsMedicalAlert(sos)) {
                String alert =
                      "[" + getNodeId() + "] ** MEDICAL ALERT ** serious case - prepare emergency beds\n"
                    + "[" + getNodeId() + "]    Emergency beds free: " + getEmergencyBeds();
                System.out.println(alert);
            } else {
                log("Case noted - no medical alert needed");
            }
        }
    }


    private boolean needsMedicalAlert(SosMessage sos) {
        if (sos.getSeverity() >= SERIOUS_SEVERITY) {
            return true;
        }
        String type = sos.getEmergencyType();
        return Constants.TYPE_ACCIDENT.equals(type)
            || Constants.TYPE_FIRE.equals(type)
            || Constants.TYPE_EARTHQUAKE.equals(type);
    }


    public Hospital getHospital() {
        return hospital;
    }

    private String getHospitalName() {
        return (hospital == null) ? "(no hospital linked)" : hospital.getHospitalName();
    }

    private String getEmergencyBeds() {
        return (hospital == null) ? "unknown" : String.valueOf(hospital.getEmergencyBeds());
    }
}
