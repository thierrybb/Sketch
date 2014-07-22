package ca.etsmtl.sketch.surface.openglshape;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.microedition.khronos.opengles.GL10;

import ca.etsmtl.sketch.common.graphic.PointF;
import ca.etsmtl.sketch.surface.transformation.MatrixWrapper;
import ca.etsmtl.sketch.utils.Identifier;

public class Drawing extends BaseShape implements Shape.ShapeListener, Iterable<Map.Entry<Identifier, Shape>> {
    private ConcurrentHashMap<Identifier, Shape> shapeIDMap = new ConcurrentHashMap<Identifier, Shape>();

    @Override
    public void draw(GL10 gl, MatrixWrapper matrix) {
        for (Shape shape : shapeIDMap.values()) {
            shape.draw(gl, matrix);
        }
    }

    @Override
    public boolean intersect(PointF pt1, PointF pt2) {
        for (Shape shape : shapeIDMap.values()) {
            if (shape.intersect(pt1, pt2))
                return true;
        }

        return false;
    }

    public void addShape(Shape shape, int shapeID, int userID) {
        shapeIDMap.put(Identifier.create(shapeID, userID), shape);
        shape.attachListener(this);
        invalide();
    }

    public void removeShape(Shape shapeToRemove) {
        List<Identifier> removeKeys = new ArrayList<Identifier>();

        for (Map.Entry<Identifier, Shape> pairShapeEntry : shapeIDMap.entrySet()) {
            if (pairShapeEntry.getValue().equals(shapeToRemove)) {
                removeKeys.add(pairShapeEntry.getKey());
            }
        }

        for (Identifier removeKey : removeKeys) {
            shapeIDMap.remove(removeKey);
        }

        invalide();
    }

    public void removeShape(int shapeID, int userID) {
        shapeIDMap.remove(Identifier.create(shapeID, userID));
        invalide();
    }

    @Override
    public void onShapeChanged() {
        invalide();
    }

    @Override
    public Iterator<Map.Entry<Identifier, Shape>> iterator() {
        return shapeIDMap.entrySet().iterator();
    }

    public Shape shapeByID(int userID, int shapeID) {
        Identifier identifier = Identifier.create(shapeID, userID);

        if (shapeIDMap.containsKey(identifier)) {
            return shapeIDMap.get(identifier);
        }

        return new NullShape();
    }
}
