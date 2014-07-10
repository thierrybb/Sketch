package ca.etsmtl.sketch.common.event;

public class OnClientKeepAlive extends OnNewClientConnected {
    public OnClientKeepAlive(String name, int id) {
        super(name, id);
    }

    public OnClientKeepAlive() {
    }
}
