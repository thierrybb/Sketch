package ca.etsmtl.sketch.surface.openglshape;

import android.graphics.RectF;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import ca.etsmtl.sketch.common.graphic.PointF;
import ca.etsmtl.sketch.surface.opengl.BufferFactory;
import ca.etsmtl.sketch.surface.opengl.OpenGLUtils;

public class InkStroke extends BaseShape {
    private FloatBuffer vertixBuffer;
    private ShortBuffer indiceBuffer;
    private int indiceCount;
    private int color = 0;

    private RectF area;
    private float[] points;

    public InkStroke(float[] points, int color) {
        this.points = points;
        vertixBuffer = BufferFactory.buildFloatBuffer(points);
        indiceCount = points.length / 2;
        short[] indices = new short[indiceCount];

        for (short i = 0; i != indiceCount; ++i)
            indices[i] = i;

        indiceBuffer = BufferFactory.buildShortBuffer(indices);

        this.color = color;
        area = OpenGLUtils.computeBounds(points);
    }

    @Override
    public void draw(GL10 gl) {
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        gl.glColor4f(red, green, blue, 1.0f);
        gl.glLineWidth(5.0f);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDisable(GL10.GL_LINE_SMOOTH);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertixBuffer);

        gl.glDrawElements(GL10.GL_LINE_STRIP, indiceCount,
                GL10.GL_UNSIGNED_SHORT, indiceBuffer);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_BLEND);
    }

    @Override
    public boolean intersect(PointF pt1, PointF pt2) {

        if (area.contains(pt1.x, pt1.y) || area.contains(pt2.x, pt2.y)
                || OpenGLUtils.isLineIntersectRect(pt1, pt2, area)) {
            for (int i = 0; i < points.length - 3; i += 2) {
                if (OpenGLUtils.intersect(pt1.x, pt1.y, pt2.x, pt2.y,
                        points[i], points[i+1], points[i+2], points[i+3])) {
                    return true;
                }
            }
        }
        return false;
    }

    public float[] getPoints() {
        return points;
    }

    public int getStrokeColor() {
        return color;
    }
}
