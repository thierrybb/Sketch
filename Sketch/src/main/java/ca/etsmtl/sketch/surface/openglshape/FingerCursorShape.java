package ca.etsmtl.sketch.surface.openglshape;

import android.graphics.Color;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import ca.etsmtl.sketch.common.graphic.PointF;
import ca.etsmtl.sketch.surface.opengl.BufferFactory;
import ca.etsmtl.sketch.surface.opengl.OpenGLUtils;

public class FingerCursorShape extends BaseShape {
    private FloatBuffer vertixBuffer;
    private ShortBuffer indiceBuffer;
    private int indiceCount;
    private int color;
    private List<PointF> points = new ArrayList<PointF>();

    public FingerCursorShape(int color) {
        init(color);
    }

    private void init(int lineColor) {
        indiceCount = 0;
        buildBuffer(1024);
        this.color = lineColor;
    }

    private void buildBuffer(int size) {
        vertixBuffer = BufferFactory.buildFloatBuffer(size * 2);
        short[] indices = new short[size];

        for (short i = 0; i != size; ++i)
            indices[i] = i;

        indiceBuffer = BufferFactory.buildShortBuffer(indices);
    }

    public void accumulatePoint(PointF point) {
        points.add(point);
        vertixBuffer.position(indiceCount * 2);
        vertixBuffer.put(point.getX());
        vertixBuffer.put(point.getY());
        vertixBuffer.position(0);
        ++indiceCount;
        invalide();
    }

    @Override
    public void draw(GL10 gl) {
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        gl.glColor4f(red, green, blue, 1.0f);
        gl.glLineWidth(1.0f);
        gl.glDisable(GL10.GL_TEXTURE_2D);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDisable(GL10.GL_LINE_SMOOTH);

        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertixBuffer);

        gl.glDrawElements(GL10.GL_LINE_STRIP, indiceCount,
                GL10.GL_UNSIGNED_SHORT, indiceBuffer);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        Circle circle = new Circle(points.get(points.size() - 1), 5, Color.BLACK, Color.CYAN);
        circle.draw(gl);
    }

    public float[] getPoints() {
        return OpenGLUtils.build2DVertices(points.toArray(new PointF[0]));
    }
}
