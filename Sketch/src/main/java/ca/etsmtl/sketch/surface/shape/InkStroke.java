package ca.etsmtl.sketch.surface.shape;

import android.graphics.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import ca.etsmtl.sketch.common.graphic.PointF;
import ca.etsmtl.sketch.surface.graphic.Graphics;

public class InkStroke extends BaseShape {
    private ArrayList<PointF> points;

    public InkStroke(float[] points) {
        this.points = new ArrayList<PointF>();

        for (int i = 0; i < points.length; i += 2) {
            this.points.add(new PointF(points[i], points[i+1]));
        }
    }

    @Override
    public void draw(Graphics graphics) {
        graphics.setLineWidth(5.0f);
        PointF lastPoint = null;

        for (PointF point : points) {
            if (lastPoint == null)
                lastPoint = point;
            else {
                graphics.drawLine(lastPoint.getX(), lastPoint.getY(), point.getX(), point.getY(), Color.BLACK);
                lastPoint = point;
            }
        }
    }
}
