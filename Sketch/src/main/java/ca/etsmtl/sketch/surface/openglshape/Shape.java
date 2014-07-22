package ca.etsmtl.sketch.surface.openglshape;

import javax.microedition.khronos.opengles.GL10;

import ca.etsmtl.sketch.common.graphic.PointF;
import ca.etsmtl.sketch.surface.transformation.MatrixWrapper;

public interface Shape {
    public interface ShapeListener {
        void onShapeChanged();

        public static ShapeListener NULL_LISTENER = new ShapeListener() {
            @Override
            public void onShapeChanged() {

            }
        };
    }

    void draw(GL10 gl, MatrixWrapper matrix);

    void attachListener(ShapeListener listener);

    boolean intersect(PointF pt1, PointF pt2);
}
