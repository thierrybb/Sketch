package ca.etsmtl.sketch.surface.opengl;

import java.util.List;

import ca.etsmtl.sketch.common.graphic.PointF;

public class OpenGLUtils {
    public static short[] indices(List<PointF> triangles, List<PointF> points) {
        short[] indices = new short[triangles.size()];
        for (int i = 0; i < triangles.size(); i++) {
            int index = points.indexOf(triangles.get(i));
            indices[i] = (short) index;
        }
        return indices;
    }

    public static float[] build2DVertices(PointF[] points) {
        float[] vertices = new float[points.length * 2];
        int index = 0;
        for (PointF mapPoint : points) {
            vertices[index] = mapPoint.getX();
            vertices[index + 1] = mapPoint.getY();
            //vertices[index + 2] = 0.0f;
            index += 2;
        }
        return vertices;
    }
}