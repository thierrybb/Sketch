package ca.etsmtl.sketch.common.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

public class OnFingerUpInkMode implements Event {
    private int fingerID;

    public OnFingerUpInkMode(int fingerID) {
        this.fingerID = fingerID;
    }

    public OnFingerUpInkMode() {
    }

    public int getFingerID() {
        return fingerID;
    }

    @Override
    public void writeInto(DataOutputStream stream) throws IOException {
        stream.writeInt(fingerID);
    }

    @Override
    public void readFrom(DataInputStream stream) throws IOException {
        fingerID = stream.readInt();
    }
}
