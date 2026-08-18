package mesh;


public class RelayNode extends MeshNode {

    public static final String ROLE_RELAY = "MESH_RELAY";


    public RelayNode(String nodeId) {
        this(nodeId, ROLE_RELAY);
    }


    public RelayNode(String nodeId, String role) {
        super(nodeId, role);
    }


    @Override
    protected void handleMessage(Message message) {


        log("Carrying " + message.getType() + " message " + message.getId()
                + " from " + message.getOriginId());
    }
}
