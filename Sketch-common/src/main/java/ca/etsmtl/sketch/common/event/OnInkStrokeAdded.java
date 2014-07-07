package ca.etsmtl.sketch.common.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;
import ca.etsmtl.sketch.common.graphic.PointF;

public class OnInkStrokeAdded implements Event {
    private float[] points;
    private int strokeColor;
    private int userID;
    private int uniqueID;

    public OnInkStrokeAdded() {

    }

    public OnInkStrokeAdded(float[] points, int strokeColor, int userID, int uniqueID) {
        this.points = points;
        this.strokeColor = strokeColor;
        this.userID = userID;
        this.uniqueID = uniqueID;
    }

    public float[] getPoints() {
        return points;
    }

    public int getStrokeColor() {
        return strokeColor;
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
        stream.writeInt(strokeColor);
        stream.writeInt(points.length);

        for (float point : points) {
            stream.writeFloat(point);
        }
    }

    @Override
    public void readFrom(ca.etsmtl.sketch.common.bus.io.DataInputStream stream) throws IOException {
        userID = stream.readInt();
        uniqueID = stream.readInt();
        strokeColor = stream.readInt();
        int pointCount = stream.readInt();
        points = new float[pointCount];

        for (int i = 0; i < pointCount; i++) {
            points[i] = stream.readFloat();
        }
    }
}
