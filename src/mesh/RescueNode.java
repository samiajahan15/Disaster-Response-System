package mesh;

import model.RescueTeam;
import util.Constants;


public class RescueNode extends MeshNode {


    private final RescueTeam rescueTeam;


    public RescueNode(String nodeId, RescueTeam rescueTeam) {
        super(nodeId, Constants.ROLE_RESCUE);
        this.rescueTeam = rescueTeam;
    }


    @Override
    protected void handleMessage(Message message) {
        super.handleMessage(message);
        if (message instanceof SosMessage) {
            SosMessage sos = (SosMessage) message;


            String report =
                  "[" + getNodeId() + "] ---- SOS ALERT FOR RESCUE TEAM ----\n"
                + "[" + getNodeId() + "]    Victim        : " + sos.getVictimName() + "\n"
                + "[" + getNodeId() + "]    Location      : " + sos.getLocation() + "\n"
                + "[" + getNodeId() + "]    Emergency type: " + sos.getEmergencyType() + "\n"
                + "[" + getNodeId() + "]    Severity      : " + sos.getSeverity() + "\n"
                + "[" + getNodeId() + "]    Responding team: " + getTeamName();
            System.out.println(report);

            log("Rescue team notified");
        }
    }


    public RescueTeam getRescueTeam() {
        return rescueTeam;
    }


    private String getTeamName() {
        return (rescueTeam == null) ? "(no team linked)" : rescueTeam.getTeamName();
    }
}
