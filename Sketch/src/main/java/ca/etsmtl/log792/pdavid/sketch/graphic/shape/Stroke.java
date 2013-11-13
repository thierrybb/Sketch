package ca.etsmtl.log792.pdavid.sketch.graphic.shape;

import java.util.ArrayList;

import ca.etsmtl.log792.pdavid.sketch.graphic.GraphicsWrapper;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.Constant;
import ca.etsmtl.log792.pdavid.sketch.graphic.util.Point2DUtil;

// This stores a polygonal line, creating by a stroke of the user's finger or pen.
public class Stroke {
    // the points that make up the stroke, in world space coordinates
    private ArrayList<Point2D> points = new ArrayList<Point2D>();

    private AlignedRectangle2D boundingRectangle = new AlignedRectangle2D();
    private boolean isBoundingRectangleDirty = false;

    private float color_red = 0;
    private float color_green = 0;
    private float color_blue = 0;

    public float getColor_red() {
        return color_red;
    }

    public float getColor_green() {
        return color_green;
    }

    public float getColor_blue() {
        return color_blue;
    }

    public String getColor() {
        String res = "";
        if (color_blue == 0 && color_green == 0 && color_red == 0) // black
            res += "b";
        else if (color_blue == 0 && color_green == 1 && color_red == 0) // green
            res += "g";
        else if (color_blue == 0 && color_green == 0 && color_red == 1) // red
            res += "r";
        return res;
    }

    public void addPoint(Point2D p) {
        points.add(p);
        isBoundingRectangleDirty = true;
    }

    public ArrayList<Point2D> getPoints() {
        return points;
    }

    public void setColor(float r, float g, float b) {
        color_red = r;
        color_green = g;
        color_blue = b;
    }

    public AlignedRectangle2D getBoundingRectangle() {
        if (isBoundingRectangleDirty) {
            boundingRectangle.clear();
            for (Point2D p : points) {
                boundingRectangle.bound(p);
            }
            isBoundingRectangleDirty = false;
        }
        return boundingRectangle;
    }

    public void markBoundingRectangleDirty() {
        isBoundingRectangleDirty = true;
    }

    public boolean isContainedInRectangle(AlignedRectangle2D r) {
        return r.contains(getBoundingRectangle());
    }

    public boolean isContainedInLassoPolygon(ArrayList<Point2D> polygonPoints) {
        for (Point2D p : points) {
            if (!Point2DUtil.isPointInsidePolygon(polygonPoints, p))
                return false;
        }
        return true;
    }

    public void draw(GraphicsWrapper gw) {
        gw.setColor(color_red, color_green, color_blue);
        gw.drawPolyline(points);
    }

    public boolean egal(Stroke s) { // TODO XXX we shouldn't need to compare
        // strokes point-by-point
        if (!(this.getColor().equals(s.getColor()))) {
            return false;
        } else if (!(this.getPoints().size() == s.getPoints().size())) {
            return false;
        } else if (!(this.getBoundingRectangle().getCenter().equals(s.getBoundingRectangle().getCenter()))) {
            return false;
        } else if (!(this.getBoundingRectangle().getMax().equals(s.getBoundingRectangle().getMax()))
                || !(this.getBoundingRectangle().getMin().equals(s.getBoundingRectangle().getMin()))) {
            return false;
        } else {
            return true;
        }
    }

    public String writeSVG(GraphicsWrapper gw) {
        String res = "<path d=\"";
        res += "M " + gw.convertWorldSpaceUnitsToPixelsX(points.get(0).x()) + " "
                + gw.convertWorldSpaceUnitsToPixelsY(points.get(0).y()) + " ";
        for (int i = 1; i < points.size(); i++) {
            res += "L " + gw.convertWorldSpaceUnitsToPixelsX(points.get(i).x()) + " "
                    + gw.convertWorldSpaceUnitsToPixelsY(points.get(i).y()) + " ";
        }
        res += "\" fill=\"none\" style=\"stroke-width: "
                + (Constant.INK_THICKNESS_IN_WORLD_SPACE_UNITS / gw.getScaleFactorInWorldSpaceUnitsPerPixel())
                + "; stroke: ";
        if (this.getColor_blue() == 0 && this.getColor_green() == 0 && this.getColor_red() == 0) {
            res += "black";
        } else if (this.getColor_blue() == 0 && this.getColor_green() == 1 && this.getColor_red() == 0) {
            res += "green";
        } else if (this.getColor_blue() == 0 && this.getColor_green() == 0 && this.getColor_red() == 1) {
            res += "red";
        }
        res += "\" />";
        return res;
    }
}
