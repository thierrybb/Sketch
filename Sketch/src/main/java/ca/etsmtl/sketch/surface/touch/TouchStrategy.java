package ca.etsmtl.sketch.surface.touch;

import ca.etsmtl.sketch.common.graphic.PointF;

public interface TouchStrategy {
    void onPointerDown(int pointerID, PointF position);
    void onPointerUp(int pointerID);
    void onPointerMove(int pointerID, PointF newPosition);
}
