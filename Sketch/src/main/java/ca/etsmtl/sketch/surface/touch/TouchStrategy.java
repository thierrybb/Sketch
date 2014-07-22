package ca.etsmtl.sketch.surface.touch;

import ca.etsmtl.sketch.common.graphic.PointF;

public interface TouchStrategy {
    void onPointerDown(int pointerID, PointF position);
    void onPointerUp(int pointerID);
    void onPointerMove(int pointerID, PointF newPosition);

    public static final TouchStrategy NULL_STRATEGY = new TouchStrategy() {
        @Override
        public void onPointerDown(int pointerID, PointF position) {

        }

        @Override
        public void onPointerUp(int pointerID) {

        }

        @Override
        public void onPointerMove(int pointerID, PointF newPosition) {

        }
    };
}
