package ca.etsmtl.sketch.surface.openglshape;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import ca.etsmtl.sketch.surface.opengl.BufferFactory;

public class InkStroke extends BaseShape {
    private FloatBuffer vertixBuffer;
    private ShortBuffer indiceBuffer;
    private int indiceCount;
    private int color = 0;

    public InkStroke(float[] points, int color) {
        vertixBuffer = BufferFactory.buildFloatBuffer(points);
        indiceCount = points.length / 2;
        short[] indices = new short[indiceCount];

        for (short i = 0; i != indiceCount; ++i)
            indices[i] = i;

        indiceBuffer = BufferFactory.buildShortBuffer(indices);

        this.color = color;
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
}
