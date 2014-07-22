package ca.etsmtl.sketch.common.bus.shapeserialization;

import java.util.HashMap;
import java.util.Map;

import ca.etsmtl.sketch.common.utils.Identifier;

public class MemorySerializer implements ShapeSerializer {
    private Map<Identifier, InkShape> allShapes = new HashMap<Identifier, InkShape>();

    @Override
    public void serializeInkStroke(float[] strokes, int strokeColor, int uniqueID, int userID) {
        allShapes.put(Identifier.create(uniqueID, userID), new InkShape(strokes, strokeColor));
    }

    @Override
    public void pullAllInkStroke(InkStoreReaderStrategy strategy) {
        for (Map.Entry<Identifier, InkShape> identifierInkShapeEntry : allShapes.entrySet()) {
            InkShape value = identifierInkShapeEntry.getValue();
            Identifier key = identifierInkShapeEntry.getKey();
            strategy.readStroke(value.getStrokes(), value.getStrokeColor(), key.getLocalID(), key.getUserID());

        }
    }

    @Override
    public void removeStroke(int shapeID, int userID) {
        allShapes.remove(Identifier.create(shapeID, userID));
    }

    private class InkShape {
        private float[] strokes;
        private int strokeColor;

        private InkShape(float[] strokes, int strokeColor) {
            this.strokes = strokes;
            this.strokeColor = strokeColor;
        }

        public float[] getStrokes() {
            return strokes;
        }

        public int getStrokeColor() {
            return strokeColor;
        }
    }
}
