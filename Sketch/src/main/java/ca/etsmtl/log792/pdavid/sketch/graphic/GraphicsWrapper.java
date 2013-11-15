package ca.etsmtl.log792.pdavid.sketch.graphic;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import java.util.ArrayList;

import ca.etsmtl.log792.pdavid.sketch.graphic.shape.AlignedRectangle2D;
import ca.etsmtl.log792.pdavid.sketch.graphic.shape.Point2D;

public class GraphicsWrapper {

    Matrix originalMatrix = null;

    private int windowWidthInPixels = 128;
    private int windowHeightInPixels = 128;
    boolean areInitialWindowDimensionsKnown = false;

    AlignedRectangle2D rectangleToFrameInitially = null;
    boolean isRectangleToFrameStoredForInitializationLater = false;

    public int getWidth() {
        return windowWidthInPixels;
    }

    public int getHeight() {
        return windowHeightInPixels;
    }

    private Paint paint = null;
    private Canvas canvas = null;
    private Rect rect = new Rect();
    private Path path = new Path();

    public void set(Paint p, Canvas c) {
        assert p != null && c != null;
        boolean firstTimeThisMethodCalled = (paint == null || canvas == null);
        this.paint = p;
        this.canvas = c;
        this.originalMatrix = c.getMatrix();

        resize(c.getWidth(), c.getHeight());

        if (firstTimeThisMethodCalled) {
            if (isFontHeightStoredForInitializationLater) {
                paint.setTextSize(fontHeight);
                isFontHeightStoredForInitializationLater = false;
            }
        }
    }

    private int fontHeight = 14;
    boolean isFontHeightStoredForInitializationLater = false;

    public void setFontHeight(int h) {
        fontHeight = h;
        if (paint != null && canvas != null) {
            paint.setTextSize(fontHeight);
            isFontHeightStoredForInitializationLater = false;
        } else {
            isFontHeightStoredForInitializationLater = true;
        }
    }

    public int getFontHeight() {
        return fontHeight;
    }

    private float offsetXInPixels = 0;
    private float offsetYInPixels = 0;
    /**
     * greater if user is more zoomed out
     */
    private float scaleFactorInWorldSpaceUnitsPerPixel = 1.0f;

    public float convertPixelsToWorldSpaceUnitsX(float XInPixels) {
        return (XInPixels - offsetXInPixels) * scaleFactorInWorldSpaceUnitsPerPixel;
    }

    public float convertPixelsToWorldSpaceUnitsY(float YInPixels) {
        return (YInPixels - offsetYInPixels) * scaleFactorInWorldSpaceUnitsPerPixel;
    }

    public Point2D convertPixelsToWorldSpaceUnits(Point2D p) {
        return new Point2D(convertPixelsToWorldSpaceUnitsX(p.x()), convertPixelsToWorldSpaceUnitsY(p.y()));
    }

    public int convertWorldSpaceUnitsToPixelsX(float x) {
        return Math.round(x / scaleFactorInWorldSpaceUnitsPerPixel + offsetXInPixels);
    }

    public int convertWorldSpaceUnitsToPixelsY(float y) {
        return Math.round(y / scaleFactorInWorldSpaceUnitsPerPixel + offsetYInPixels);
    }

    public Point2D convertWorldSpaceUnitsToPixels(Point2D p) {
        return new Point2D(convertWorldSpaceUnitsToPixelsX(p.x()), convertWorldSpaceUnitsToPixelsY(p.y()));
    }

    public float getScaleFactorInWorldSpaceUnitsPerPixel() {
        return scaleFactorInWorldSpaceUnitsPerPixel;
    }

    public void pan(float dx, float dy) {
        offsetXInPixels += dx;
        offsetYInPixels += dy;
    }

    /**
     * Greater than 1 to zoom in, between 0 and 1 to zoom out
     *
     * @param zoomFactor
     */
    public void zoomIn(float zoomFactor, float centerXInPixels, float centerYInPixels) {
        scaleFactorInWorldSpaceUnitsPerPixel /= zoomFactor;
        offsetXInPixels = centerXInPixels - (centerXInPixels - offsetXInPixels) * zoomFactor;
        offsetYInPixels = centerYInPixels - (centerYInPixels - offsetYInPixels) * zoomFactor;
    }

    /**
     * Greater than 1 to zoom in, between 0 and 1 to zoom out
     *
     * @param zoomFactor
     */
    public void zoomIn(float zoomFactor) {
        zoomIn(zoomFactor, windowWidthInPixels * 0.5f, windowHeightInPixels * 0.5f);
    }

    /**
     * This can be used to implement bimanual (2-handed) camera control,or 2-finger camera control, as in a "pinch" gesture
     *
     * @param A_new assumed to be in pixel coordinates
     * @param A_old assumed to be in pixel coordinates
     * @param B_new assumed to be in pixel coordinates
     * @param B_old assumed to be in pixel coordinates
     */
    public void panAndZoomBasedOnDisplacementOfTwoPoints(Point2D A_old, Point2D B_old, Point2D A_new, Point2D B_new) {

        // Compute midpoints of each pair of points
        Point2D M1 = Point2D.average(A_old, B_old);
        Point2D M2 = Point2D.average(A_new, B_new);

        // This is the translation that the world should appear to undergo.
        Vector2D translation = Point2D.diff(M2, M1);

        // Compute a vector associated with each pair of points.
        Vector2D v1 = Point2D.diff(A_old, B_old);
        Vector2D v2 = Point2D.diff(A_new, B_new);

        float v1_length = v1.length();
        float v2_length = v2.length();
        float scaleFactor = 1;
        if (v1_length > 0 && v2_length > 0)
            scaleFactor = v2_length / v1_length;
        pan(translation.x(), translation.y());
        zoomIn(scaleFactor, M2.x(), M2.y());
    }

    /**
     * Creates a frame in the specified rectangle
     *
     * @param rect
     * @param expand true if caller wants a margin of whitespace added around the rect
     */
    public void frame(AlignedRectangle2D rect, boolean expand) {
        if (rect.isEmpty() || rect.getDiagonal().x() == 0 || rect.getDiagonal().y() == 0) {
            return;
        }
        if (expand) {
            float diagonal = rect.getDiagonal().length() / 20;
            Vector2D v = new Vector2D(diagonal, diagonal);
            rect = new AlignedRectangle2D(Point2D.diff(rect.getMin(), v), Point2D.sum(rect.getMax(), v));
        }

        if (!areInitialWindowDimensionsKnown) {
            rectangleToFrameInitially = new AlignedRectangle2D(rect);
            isRectangleToFrameStoredForInitializationLater = true;
        } else {
            assert windowWidthInPixels > 0 && windowHeightInPixels > 0;

            if (rect.getDiagonal().x() / rect.getDiagonal().y() >= windowWidthInPixels / (float) windowHeightInPixels) {
                offsetXInPixels = -rect.getMin().x() * windowWidthInPixels / rect.getDiagonal().x();
                scaleFactorInWorldSpaceUnitsPerPixel = rect.getDiagonal().x() / windowWidthInPixels;
                offsetYInPixels = windowHeightInPixels / 2 - rect.getCenter().y()
                        / scaleFactorInWorldSpaceUnitsPerPixel;
            } else {
                offsetYInPixels = -rect.getMin().y() * windowHeightInPixels / rect.getDiagonal().y();
                scaleFactorInWorldSpaceUnitsPerPixel = rect.getDiagonal().y() / windowHeightInPixels;
                offsetXInPixels = windowWidthInPixels / 2 - rect.getCenter().x() / scaleFactorInWorldSpaceUnitsPerPixel;
            }
        }
    }

    private void setInitialWindowDimensions(int w, int h) {
        assert !areInitialWindowDimensionsKnown;
        windowWidthInPixels = w;
        windowHeightInPixels = h;
        areInitialWindowDimensionsKnown = true;
        if (isRectangleToFrameStoredForInitializationLater) {
            isRectangleToFrameStoredForInitializationLater = false;
            frame(rectangleToFrameInitially, false);
            rectangleToFrameInitially = null;
        }
    }

    public void resize(int w, int h) {
        if (!areInitialWindowDimensionsKnown) {
            setInitialWindowDimensions(w, h);
        } else if (w != windowWidthInPixels || h != windowHeightInPixels) {
            Point2D oldCenter = convertPixelsToWorldSpaceUnits(new Point2D(windowWidthInPixels * 0.5f,
                    windowHeightInPixels * 0.5f));
            float radius = Math.min(windowWidthInPixels, windowHeightInPixels) * 0.5f
                    * scaleFactorInWorldSpaceUnitsPerPixel;

            windowWidthInPixels = w;
            windowHeightInPixels = h;

            if (radius > 0) {
                frame(new AlignedRectangle2D(new Point2D(oldCenter.x() - radius, oldCenter.y() - radius), new Point2D(
                        oldCenter.x() + radius, oldCenter.y() + radius)), false);
            }
        }
    }

    public void setCoordinateSystemToPixels() {
        canvas.setMatrix(originalMatrix);
    }

    public void setCoordinateSystemToWorldSpaceUnits() {
        canvas.setMatrix(originalMatrix);
        canvas.translate(offsetXInPixels, offsetYInPixels);
        float s = 1.0f / scaleFactorInWorldSpaceUnitsPerPixel;
        canvas.scale(s, s);
    }

    public void clear(float r, float g, float b) {
        canvas.drawRGB((int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    public void setColor(int c) {
        paint.setColor(c);
    }

    public void setColor(float r, float g, float b) {
        paint.setARGB(255, (int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    public void setColor(float r, float g, float b, float alpha) {
        paint.setARGB((int) (alpha * 255), (int) (r * 255), (int) (g * 255), (int) (b * 255));
    }

    public void setLineWidth(float width) {
        paint.setStrokeWidth(width);
    }

    public void drawLine(float x1, float y1, float x2, float y2) {
        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    public void drawPolyline(ArrayList<Point2D> points, boolean isClosed, boolean isFilled) {
        if (points.size() <= 1) {
            return;
        }

        path.reset();
        Point2D p = points.get(0);
        path.moveTo(p.x(), p.y());
        for (int i = 1; i < points.size(); ++i) {
            p = points.get(i);
            path.lineTo(p.x(), p.y());
        }
        if (isClosed)
            path.close();
        paint.setStyle(isFilled ? Paint.Style.FILL : Paint.Style.STROKE);
        // TODO FIXME XXX or should i call path.setFillType() ?
        canvas.drawPath(path, paint);
    }

    public void drawPolyline(ArrayList<Point2D> points) {
        drawPolyline(points, false, false);
    }

    public void drawPolygon(ArrayList<Point2D> points) {
        drawPolyline(points, true, false);
    }

    public void fillPolygon(ArrayList<Point2D> points) {
        drawPolyline(points, true, true);
    }

    public void drawRect(float x, float y, float w, float h, boolean isFilled) {
        paint.setStyle(isFilled ? Paint.Style.FILL : Paint.Style.STROKE);
        canvas.drawRect(x, y, x + w, y + h, paint);
    }

    public void drawRect(float x, float y, float w, float h) {
        drawRect(x, y, w, h, false);
    }

    public void fillRect(float x, float y, float w, float h) {
        drawRect(x, y, w, h, true);
    }

    public void drawCircle(float x, float y, float radius, boolean isFilled) {
        paint.setStyle(isFilled ? Paint.Style.FILL : Paint.Style.STROKE);
        canvas.drawCircle(x, y, radius, paint);
    }

    public void drawCircle(float x, float y, float radius) {
        drawCircle(x, y, radius, false);
    }

    public void fillCircle(float x, float y, float radius) {
        drawCircle(x, y, radius, true);
    }

    public void drawCenteredCircle(float x, float y, float radius, boolean isFilled) {
        x -= radius;
        y -= radius;
        drawCircle(x, y, radius, isFilled);
    }

    /**
     * @param center_x    increases right
     * @param center_y    increases down
     * @param radius      in radians, zero for right, increasing counterclockwise
     * @param startAngle  in radians, zero for right, increasing counterclockwise
     * @param angleExtent in radians; positive for counterclockwise
     * @param isFilled
     */
    public void drawArc(float center_x, float center_y, float radius, float startAngle, float angleExtent, boolean isFilled) {
        // TODO FIXME XXX
    }

    /**
     * @param center_x
     * @param center_y
     * @param radius      in radians
     * @param startAngle  in radians
     * @param angleExtent
     */
    public void drawArc(float center_x, float center_y, float radius, float startAngle, float angleExtent) {
        drawArc(center_x, center_y, radius, startAngle, angleExtent, false);
    }

    public void fillArc(float center_x, float center_y, float radius, float startAngle, float angleExtent) {
        drawArc(center_x, center_y, radius, startAngle, angleExtent, true);
    }

    /**
     * @param s the string to measure
     * @return the width of a string
     */
    public float stringWidth(String s) {
        if (s == null || s.length() == 0)
            return 0;

        paint.getTextBounds(s, 0, s.length(), rect);
        return rect.width();
    }

    /**
     * @param x
     * @param y lower left corner of the string
     * @param s the string
     */
    public void drawString(float x, float y, String s) {
        if (s == null || s.length() == 0) {
            return;
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(s, x, y, paint);
    }

}
