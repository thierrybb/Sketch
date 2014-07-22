package ca.etsmtl.sketch.common.bus.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

public class OnClientDisconnected implements Event {
    private int userID;

    public OnClientDisconnected(int userID) {
        this.userID = userID;
    }

    public OnClientDisconnected() {
    }

    public int getUserID() {
        return userID;
    }

    @Override
    public void writeInto(DataOutputStream stream) throws IOException {
        stream.writeInt(userID);
    }

    @Override
    public void readFrom(DataInputStream stream) throws IOException {
        userID = stream.readInt();
    }
}
