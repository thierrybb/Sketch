package ca.etsmtl.sketch.common.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;
import ca.etsmtl.sketch.common.graphic.PointF;

public class OnFingerMoveInkMode implements Event {
    private int fingerID;
    private PointF position;
    private int userID;

    public OnFingerMoveInkMode(int fingerID, PointF position, int userID) {
        this.fingerID = fingerID;
        this.position = position;
        this.userID = userID;
    }

    public OnFingerMoveInkMode() {
    }

    public int getFingerID() {
        return fingerID;
    }

    public PointF getPosition() {
        return position;
    }

    public int getUserID() {
        return userID;
    }

    @Override
    public void writeInto(DataOutputStream stream) throws IOException {
        stream.writeInt(fingerID);
        stream.writeFloat(position.getX());
        stream.writeFloat(position.getY());
        stream.writeInt(userID);
    }

    @Override
    public void readFrom(DataInputStream stream) throws IOException {
        fingerID = stream.readInt();
        position = new PointF(stream.readFloat(), stream.readFloat());
        userID = stream.readInt();
    }
}
