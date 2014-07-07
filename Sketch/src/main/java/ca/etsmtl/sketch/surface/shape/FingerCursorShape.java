package ca.etsmtl.sketch.surface.shape;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import ca.etsmtl.sketch.common.graphic.PointF;
import ca.etsmtl.sketch.surface.graphic.Graphics;
import ca.etsmtl.sketch.surface.opengl.OpenGLUtils;

public class FingerCursorShape extends BaseShape {
    private ArrayList<PointF> points = new ArrayList<PointF>();

    public FingerCursorShape(PointF startPoint) {
        points.add(startPoint);
    }

    public void accumulatePoint(PointF point) {
        points.add(point);
        invalide();
    }

    public float[] getPoints() {
        return OpenGLUtils.build2DVertices(points.toArray(new PointF[0]));
    }

    @Override
    public void draw(Graphics graphics) {
        graphics.setLineWidth(1.0f);
        PointF lastPoint = null;

        for (PointF point : points) {
            if (lastPoint == null)
                lastPoint = point;
            else {
                graphics.drawLine(lastPoint.getX(), lastPoint.getY(), point.getX(), point.getY(), Color.BLACK);
                lastPoint = point;
            }
        }

        graphics.fillCircle(lastPoint.getX(), lastPoint.getY(), 10, Color.CYAN);
    }
}
