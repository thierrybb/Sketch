package ca.etsmtl.sketch.common.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

public class OnInkStrokeErased extends OnInkStrokeRemoved {
    private int eraserUserID;

    public OnInkStrokeErased() {
    }

    public OnInkStrokeErased(int userID, int uniqueID, int eraserUserID) {
        super(userID, uniqueID);
        this.eraserUserID = eraserUserID;
    }

    public int getEraserUserID() {
        return eraserUserID;
    }

    @Override
    public void writeInto(DataOutputStream stream) throws IOException {
        super.writeInto(stream);
        stream.writeInt(eraserUserID);
    }

    @Override
    public void readFrom(DataInputStream stream) throws IOException {
        super.readFrom(stream);
        eraserUserID = stream.readInt();
    }
}
