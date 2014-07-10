package ca.etsmtl.sketch.common.event;

public class OnInkStrokeReAdded extends OnInkStrokeAdded {

    public OnInkStrokeReAdded(float[] points, int strokeColor, int userID, int uniqueID) {
        super(points, strokeColor, userID, uniqueID);
    }

    public OnInkStrokeReAdded() {
    }
}
