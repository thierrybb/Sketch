package ca.etsmtl.sketch.common.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

public class OnStrokeRestored extends OnInkStrokeAdded {
    private int destinationUserID;

    public OnStrokeRestored() {
    }

    public OnStrokeRestored(float[] points, int strokeColor, int userID, int uniqueID, int destinationUserID) {
        super(points, strokeColor, userID, uniqueID);
        this.destinationUserID = destinationUserID;
    }

    @Override
    public void writeInto(DataOutputStream stream) throws IOException {
        super.writeInto(stream);
        stream.writeInt(destinationUserID);
    }

    @Override
    public void readFrom(DataInputStream stream) throws IOException {
        super.readFrom(stream);
        destinationUserID = stream.readInt();
    }

    public int getDestinationUserID() {
        return destinationUserID;
    }
}
