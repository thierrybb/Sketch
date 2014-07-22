package ca.etsmtl.sketch.surface.transformation;

import android.graphics.Matrix;

import javax.microedition.khronos.opengles.GL10;

import ca.etsmtl.sketch.common.graphic.PointF;

public class MatrixWrapper {
    private Matrix savedMatrix = new Matrix();
    private Matrix matrix = new Matrix();

    private float[] matrixValues = new float[9];

    public void postScale(float newScale, PointF zoomCenterPoint) {
        matrix.postScale(newScale, newScale, zoomCenterPoint.x, zoomCenterPoint.y);
    }

    public void postTranslation(float xOffset, float yOffset) {
        matrix.postTranslate(xOffset, yOffset);
    }

    public void applyToOpenGL(GL10 gl) {
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        matrix.getValues(matrixValues);

        gl.glTranslatef(matrixValues[Matrix.MTRANS_X], matrixValues[Matrix.MTRANS_Y], 1);
        gl.glScalef(matrixValues[Matrix.MSCALE_X], matrixValues[Matrix.MSCALE_Y], 0.0f);
    }

    public PointF convertPointFromView(PointF point) {
        Matrix inverse = new Matrix();
        matrix.invert(inverse);
        float[] coordinateToConvert = new float[] { point.x, point.y };
        inverse.mapPoints(coordinateToConvert);
        point.set(coordinateToConvert[0], coordinateToConvert[1]);
        return point;
    }

    public void save() {
        savedMatrix.set(matrix);
    }

    public void restore() {
        matrix.set(savedMatrix);
    }
}
