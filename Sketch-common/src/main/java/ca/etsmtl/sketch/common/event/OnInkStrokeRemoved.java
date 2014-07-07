package ca.etsmtl.sketch.common.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

public class OnInkStrokeRemoved implements Event {
    private int userID;
    private int uniqueID;

    public OnInkStrokeRemoved() {
    }

    public OnInkStrokeRemoved(int userID, int uniqueID) {
        this.userID = userID;
        this.uniqueID = uniqueID;
    }

    public int getUserID() {
        return userID;
    }

    public int getUniqueID() {
        return uniqueID;
    }

    @Override
    public void writeInto(DataOutputStream stream) throws IOException {
        stream.writeInt(userID);
        stream.writeInt(uniqueID);
    }

    @Override
    public void readFrom(DataInputStream stream) throws IOException {
        userID = stream.readInt();
        uniqueID = stream.readInt();
    }
}
