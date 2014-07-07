package ca.etsmtl.sketch.common.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;
import ca.etsmtl.sketch.common.graphic.PointF;

public class OnInkDrawingAdded implements Event {
    private float[] points;

    public OnInkDrawingAdded() {

    }

    public OnInkDrawingAdded(float[] points) {
        this.points = points;
    }

    public float[] getPoints() {
        return points;
    }

    @Override
    public void writeInto(DataOutputStream stream) throws IOException {
        stream.writeInt(points.length);

        for (float point : points) {
            stream.writeFloat(point);
        }
    }

    @Override
    public void readFrom(ca.etsmtl.sketch.common.bus.io.DataInputStream stream) throws IOException {
        int pointCount = stream.readInt();
        points = new float[pointCount];

        for (int i = 0; i < pointCount; i++) {
            points[i] = stream.readFloat();
        }
    }
}
