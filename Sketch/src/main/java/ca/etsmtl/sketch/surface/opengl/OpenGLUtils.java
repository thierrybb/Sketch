package ca.etsmtl.sketch.surface.opengl;

import android.graphics.RectF;

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

    public static boolean intersect(float l1x1, float l1y1, float l1x2, float l1y2, float l2x1, float l2y1, float l2x2,
                                    float l2y2) {
        float denom = ((l2y2 - l2y1) * (l1x2 - l1x1)) - ((l2x2 - l2x1) * (l1y2 - l1y1));

        if (denom == 0.0f) {
            return false;
        }

        float ua = (((l2x2 - l2x1) * (l1y1 - l2y1)) - ((l2y2 - l2y1) * (l1x1 - l2x1))) / denom;
        float ub = (((l1x2 - l1x1) * (l1y1 - l2y1)) - ((l1y2 - l1y1) * (l1x1 - l2x1))) / denom;

        return ((ua >= 0.0d) && (ua <= 1.0d) && (ub >= 0.0d) && (ub <= 1.0d));
    }

    public static boolean isLineIntersectRect(PointF linePt1, PointF linePt2, RectF rectangle) {

        // Top line
        if (intersect(linePt1.x, linePt1.y, linePt2.x, linePt2.y,
                rectangle.left,
                rectangle.top,
                rectangle.right,
                rectangle.top))
            return true;
        // Bottom line
        if (intersect(linePt1.x, linePt1.y, linePt2.x, linePt2.y,
                rectangle.left,
                rectangle.bottom,
                rectangle.right,
                rectangle.bottom))
            return true;
        // Left side...
        if (intersect(linePt1.x, linePt1.y, linePt2.x, linePt2.y,
                rectangle.left,
                rectangle.top,
                rectangle.left,
                rectangle.bottom))
            return true;
        // Right side
        if (intersect(linePt1.x, linePt1.y, linePt2.x, linePt2.y,
                rectangle.right,
                rectangle.top,
                rectangle.right,
                rectangle.bottom))
            return true;

        return false;

    }

    public static RectF computeBounds(float[] points) {
        RectF bounds = new RectF();

        if (points.length > 2) {
            bounds.top = points[1];
            bounds.left = points[0];
        }

        for (int i = 2; i < points.length - 1; i += 2) {
            float x = points[i];
            float y = points[i + 1];
            if (bounds.top > y)
                bounds.top = y;

            if (bounds.bottom < y)
                bounds.bottom = y;

            if (bounds.left > x)
                bounds.left = x;

            if (bounds.right < x)
                bounds.right = x;
        }

        return bounds;
    }
}