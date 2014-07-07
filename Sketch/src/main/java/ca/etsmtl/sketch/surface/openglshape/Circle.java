package ca.etsmtl.sketch.surface.openglshape;

import android.graphics.RectF;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import ca.etsmtl.sketch.common.graphic.PointF;
import ca.etsmtl.sketch.surface.opengl.BufferFactory;

import static android.util.FloatMath.cos;
import static android.util.FloatMath.sin;

class Circle extends BaseShape {
    private PointF center;
    private float radius;
    private final int borderColor;
    private final int fillColor;
    private int vertexCount = 10;
    private int outerVertexCount = vertexCount - 1;
    private FloatBuffer vertixBuffer;
    private ShortBuffer indicesBuffer;
    private int indicesCount;

    public Circle(PointF center, float radius, int borderColor, int fillColor) {
        this.center = center;
        this.radius = radius;
        this.borderColor = borderColor;
        this.fillColor = fillColor;

        initShape();
    }

    private void initShape() {
        //create a buffer for vertex data
        float buffer[] = new float[vertexCount * 2]; // (x,y) for each vertex
        short indices[] = new short[vertexCount * 3];
        int idx = 0;
        int indicesIndex = 0;

        //center vertex for triangle fan
        buffer[idx++] = center.x;
        buffer[idx++] = center.y;

        //outer vertices of the circle
        for (int i = 0; i < outerVertexCount; ++i) {
            float percent = (i / (float) (outerVertexCount - 1));
            float rad = percent * 2.0f * (float) Math.PI;

            //vertex position
            float outerX = center.x + radius * cos(rad);
            float outerY = center.y + radius * sin(rad);

            buffer[idx++] = outerX;
            buffer[idx++] = outerY;

            indices[indicesIndex++] = 0;
            indices[indicesIndex++] = (short) i;
            indices[indicesIndex++] = (short) (i + 1);
        }

        indices[indicesIndex - 1] = 1;

        indicesCount = indices.length;

        vertixBuffer = BufferFactory.buildFloatBuffer(buffer);
        indicesBuffer = BufferFactory.buildShortBuffer(indices);
    }

    @Override
    public void draw(GL10 gl) {
        gl.glEnable(GL10.GL_BLEND);
        gl.glEnable(GL10.GL_LINE_SMOOTH);
        // Enabled the vertices buffer for writing and to be used during
        // rendering.
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // Specifies the location and data format of an array of vertex
        // coordinates to use when rendering.
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertixBuffer);
        setColor(gl, fillColor);

        gl.glDrawElements(GL10.GL_TRIANGLES, indicesCount,
                GL10.GL_UNSIGNED_SHORT, indicesBuffer);


        gl.glLineWidth(2.0f);
        setColor(gl, borderColor);

        //draw circle contours (skip center vertex at start of buffer)
        gl.glDrawArrays(gl.GL_LINE_LOOP, 2, outerVertexCount - 1);

        // Disable the vertices buffer.
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_LINE_SMOOTH);
        gl.glDisable(GL10.GL_BLEND);
    }

    private void setColor(GL10 gl, int color) {
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;
        gl.glColor4f(red, green, blue, alpha);
    }
}
