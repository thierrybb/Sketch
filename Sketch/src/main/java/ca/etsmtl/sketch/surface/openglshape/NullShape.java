package ca.etsmtl.sketch.surface.openglshape;

import javax.microedition.khronos.opengles.GL10;

import ca.etsmtl.sketch.common.graphic.PointF;
import ca.etsmtl.sketch.surface.transformation.MatrixWrapper;

public class NullShape implements Shape {
    @Override
    public void draw(GL10 gl, MatrixWrapper matrix) {

    }

    @Override
    public void attachListener(ShapeListener listener) {

    }

    @Override
    public boolean intersect(PointF pt1, PointF pt2) {
        return false;
    }
}
