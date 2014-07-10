package ca.etsmtl.sketch.common.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

public class OnFingerUpInkMode implements Event {
    private int fingerID;
    private int userID;

    public OnFingerUpInkMode(int fingerID, int userID) {
        this.fingerID = fingerID;
        this.userID = userID;
    }

    public OnFingerUpInkMode() {
    }

    public int getFingerID() {
        return fingerID;
    }

    public int getUserID() {
        return userID;
    }

    @Override
    public void writeInto(DataOutputStream stream) throws IOException {
        stream.writeInt(fingerID);
        stream.writeInt(userID);
    }

    @Override
    public void readFrom(DataInputStream stream) throws IOException {
        fingerID = stream.readInt();
        userID = stream.readInt();
    }
}
