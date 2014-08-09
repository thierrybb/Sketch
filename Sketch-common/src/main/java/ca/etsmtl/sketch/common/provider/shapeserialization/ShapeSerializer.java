package ca.etsmtl.sketch.common.provider.shapeserialization;

public interface ShapeSerializer {
    void serializeInkStroke(float[] strokes, int strokeColor, int uniqueID, int userID);

    void pullAllInkStroke(InkStoreReaderStrategy strategy);

    void removeStroke(int shapeID, int userID);

    public interface InkStoreReaderStrategy {
        void readStroke(float[] strokes, int color, int id, int userID);
    }
}
