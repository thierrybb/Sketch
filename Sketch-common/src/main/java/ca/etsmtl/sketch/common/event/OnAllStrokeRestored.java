package ca.etsmtl.sketch.common.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

public class OnAllStrokeRestored implements Event {
    private int destinationUserID;

    public OnAllStrokeRestored() {
    }

    public OnAllStrokeRestored(int destinationUserID) {
        this.destinationUserID = destinationUserID;
    }

    public int getDestinationUserID() {
        return destinationUserID;
    }

    @Override
    public void writeInto(DataOutputStream stream) throws IOException {
        stream.writeInt(destinationUserID);
    }

    @Override
    public void readFrom(DataInputStream stream) throws IOException {
        destinationUserID = stream.readInt();
    }
}
