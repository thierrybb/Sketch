package ca.etsmtl.sketch.common.bus.shapeserialization;

public interface ShapeSerializer {
    void serializeInkStroke(float[] strokes, int color);

    void pullAllInkStroke(InkStoreReaderStrategy strategy);

    public interface InkStoreReaderStrategy {
        void readStroke(float[] strokes, int color, int id, int userID);
    }
}
