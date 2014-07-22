package ca.etsmtl.sketch.common.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;


public class OnSyncRequireEvent implements Event {
    private int userID;

    public OnSyncRequireEvent() {
    }

    public OnSyncRequireEvent(int userID) {
        this.userID = userID;
    }

    @Override
    public void writeInto(DataOutputStream stream) throws IOException {
        stream.writeInt(userID);
    }

    @Override
    public void readFrom(DataInputStream stream) throws IOException {
        userID = stream.readInt();
    }

    public int getUserID() {
        return userID;
    }
}
