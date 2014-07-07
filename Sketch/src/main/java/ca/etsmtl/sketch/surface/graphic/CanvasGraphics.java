package ca.etsmtl.sketch.surface.graphic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Collection;

import ca.etsmtl.sketch.common.graphic.PointF;

public class CanvasGraphics implements Graphics {
    private Canvas canvas;
    private Paint paint = new Paint();

    public CanvasGraphics() {
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void drawLine(float x1, float y1, float x2, float y2, int color) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    @Override
    public void drawLines(Collection<PointF> points, int color) {
        PointF lastPoint = null;

        for (PointF point : points) {
            if (lastPoint == null)
                lastPoint = point;
            else {
                drawLine(lastPoint.getX(), lastPoint.getY(), point.getX(), point.getY(), Color.BLACK);
                lastPoint = point;
            }
        }
    }

    @Override
    public void setLineWidth(float width) {
        paint.setStrokeWidth(width);
    }

    @Override
    public void fillCircle(float centerX, float centerY, float radius, int color) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(centerX, centerY, radius, paint);
    }
}
