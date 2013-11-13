package ca.etsmtl.log792.pdavid.sketch.graphic;

import java.util.ArrayList;

import ca.etsmtl.log792.pdavid.sketch.graphic.shape.Point2D;

// This stores a set of instances of MyCursor.
// Each cursor can be identified by its id,
// which is assigned by the framework or computing platform.
// Each cursor can also be identified by its index in this class's container.
// For example, if an instance of this class is storing 3 cursors,
// their ids may be 2, 18, 7,
// but their indices should be 0, 1, 2.
public class CursorContainer {
    private ArrayList<MyCursor> cursors = new ArrayList<MyCursor>();

    public int getNumCursors() {
        return cursors.size();
    }

    public MyCursor getCursorByIndex(int index) {
        return cursors.get(index);
    }

    public int findIndexOfCursorById(int id) {
        for (int i = 0; i < cursors.size(); ++i) {
            if (cursors.get(i).id == id)
                return i;
        }
        return -1;
    }

    public MyCursor getCursorById(int id) {
        int index = findIndexOfCursorById(id);
        return (index == -1) ? null : cursors.get(index);
    }

    // Returns the number of cursors that are of the given type.
    public int getNumCursorsOfGivenType(int type) {
        int num = 0;
        for (int i = 0; i < cursors.size(); ++i) {
            if (cursors.get(i).getType() == type)
                num++;
        }
        return num;
    }

    // Returns the (i)th cursor of the given type,
    // or null if no such cursor exists.
    // Can be used for retrieving both cursors of type TYPE_CAMERA_PAN_ZOOM, for
    // example,
    // by calling getCursorByType( MyCursor.TYPE_CAMERA_PAN_ZOOM, 0 )
    // and getCursorByType( MyCursor.TYPE_CAMERA_PAN_ZOOM, 1 ),
    // when there may be cursors of other type present at the same time.
    public MyCursor getCursorByType(int type, int i) {
        for (int ii = 0; ii < cursors.size(); ++ii) {
            if (cursors.get(ii).getType() == type) {
                if (i == 0)
                    return cursors.get(ii);
                else
                    i--;
            }
        }
        return null;
    }

    // Returns index of updated cursor.
    // If a cursor with the given id does not already exist, a new cursor for it
    // is created.
    public int updateCursorById(int id, float x, float y) {
        Point2D updatedPosition = new Point2D(x, y);
        int index = findIndexOfCursorById(id);
        if (index == -1) {
            cursors.add(new MyCursor(id, x, y));
            index = cursors.size() - 1;
        }
        MyCursor c = cursors.get(index);
        if (!c.getCurrentPosition().equals(updatedPosition)) {
            c.addPosition(updatedPosition);
        }
        return index;
    }

    public void removeCursorByIndex(int index) {
        cursors.remove(index);
    }

    public ArrayList<Point2D> getWorldPositionsOfCursors(GraphicsWrapper gw) {
        ArrayList<Point2D> positions = new ArrayList<Point2D>();
        for (MyCursor cursor : cursors) {
            positions.add(gw.convertPixelsToWorldSpaceUnits(cursor.getCurrentPosition()));
        }
        return positions;
    }
}
