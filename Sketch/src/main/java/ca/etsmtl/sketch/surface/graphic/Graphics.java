package ca.etsmtl.sketch.surface.graphic;

import java.util.Collection;

import ca.etsmtl.sketch.common.graphic.PointF;

public interface Graphics {
    void drawLine(float x1, float y1, float x2, float y2, int color);

    void drawLines(Collection<PointF> points, int color);

    void setLineWidth(float width);

    void fillCircle(float centerX, float centerY, float radius, int color);
}
