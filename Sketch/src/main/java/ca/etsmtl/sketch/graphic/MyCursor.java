package ca.etsmtl.sketch.graphic;

import java.util.ArrayList;

import ca.etsmtl.sketch.graphic.shape.Point2D;

// This class stores the current position of a finger,
// as well as the history of previous positions of that finger
// during its drag.
//
// An instance of this class is created when a finger makes contact
// with the multitouch surface. The instance stores all
// subsequent positions of the finger, and is destroyed
// when the finger is lifted off the multitouch surface.
public class MyCursor {

    // These are used to store what the cursor is being used for.
    public static final int TYPE_NOTHING = 0; // in this case, the cursor is
    public int type = TYPE_NOTHING;
    // ignored
    public static final int TYPE_INTERACTING_WITH_WIDGET = 1; // interacting
    // with a
    // virtual
    // button, menu,
    // etc.
    public static final int TYPE_INKING = 2; // creating a stroke
    public static final int TYPE_CAMERA_PAN_ZOOM = 3;
    public static final int TYPE_SELECTION = 4;
    public static final int TYPE_DIRECT_MANIPULATION = 5;
    // Each finger in contact with the multitouch surface is given
    // a unique id by the framework (or computing platform).
    // There is no guarantee that these ids will be consecutive nor increasing.
    // For example, when two fingers are in contact with the multitouch surface,
    // their ids may be 0 and 1, respectively,
    // or their ids may be 14 and 9, respectively.
    public int id; // identifier
    // This is only used if (type == TYPE_INTERACTING_WITH_WIDGET),
    // in which case it stores the index of the palette button under the cursor.
    public int indexOfButton = 0;
    // This stores the history of positions of the "cursor" (finger)
    // in pixel coordinates.
    // The first position is where the finger pressed down,
    // and the last position is the current position of the finger.
    private ArrayList<Point2D> positions = new ArrayList<Point2D>();
    private float totalDistanceAlongDrag = 0;
    private float distanceFromStartToEndOfDrag = 0;

    public MyCursor(int id, float x, float y) {
        this.id = id;
        positions.add(new Point2D(x, y));
    }

    public ArrayList<Point2D> getPositions() {
        return positions;
    }

    public void addPosition(Point2D p) {
        if (positions.size() >= 1) {
            totalDistanceAlongDrag += p.distance(positions.get(positions.size() - 1));
            distanceFromStartToEndOfDrag = p.distance(positions.get(0));
        }
        positions.add(p);
    }

    public Point2D getFirstPosition() {
        if (positions == null || positions.size() < 1)
            return null;
        return positions.get(0);
    }

    public Point2D getCurrentPosition() {
        if (positions == null || positions.size() < 1)
            return null;
        return positions.get(positions.size() - 1);
    }

    public Point2D getPreviousPosition() {
        if (positions == null || positions.size() == 0)
            return null;
        if (positions.size() == 1)
            return positions.get(0);
        return positions.get(positions.size() - 2);
    }

    public boolean doesDragLookLikeLassoGesture() {
        return totalDistanceAlongDrag / (float) distanceFromStartToEndOfDrag > 2.5f;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * @param type          only used if (type == TYPE_INTERACTING_WITH_WIDGET)
     * @param indexOfButton
     */
    public void setType(int type, int indexOfButton) {
        this.type = type;
        this.indexOfButton = indexOfButton;
    }
}
