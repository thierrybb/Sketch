package ca.etsmtl.sketch.common.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;
import ca.etsmtl.sketch.common.graphic.PointF;

public class OnFingerDownInkMode implements Event {
    private int fingerID;
    private PointF position;

    public OnFingerDownInkMode(int fingerID, PointF position) {
        this.fingerID = fingerID;
        this.position = position;
    }

    public OnFingerDownInkMode() {
    }

    public int getFingerID() {
        return fingerID;
    }

    public PointF getPosition() {
        return position;
    }

    @Override
    public void writeInto(DataOutputStream stream) throws IOException {
        stream.writeInt(fingerID);
        stream.writeFloat(position.getX());
        stream.writeFloat(position.getY());
    }

    @Override
    public void readFrom(DataInputStream stream) throws IOException {
        fingerID = stream.readInt();
        position = new PointF(stream.readFloat(), stream.readFloat());
    }
}
