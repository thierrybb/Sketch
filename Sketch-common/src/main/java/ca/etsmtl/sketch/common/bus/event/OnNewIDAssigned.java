package ca.etsmtl.sketch.common.bus.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

public class OnNewIDAssigned implements Event {
    private int newID;

    public OnNewIDAssigned() {
    }

    public OnNewIDAssigned(int newID) {
        this.newID = newID;
    }

    @Override
    public void writeInto(DataOutputStream stream) throws IOException {
        stream.writeInt(newID);
    }

    @Override
    public void readFrom(DataInputStream stream) throws IOException {
        newID = stream.readInt();
    }

    public int getNewID() {
        return newID;
    }
}
