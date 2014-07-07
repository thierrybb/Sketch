package ca.etsmtl.sketch.surface.openglshape;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.microedition.khronos.opengles.GL10;

public class Drawing extends BaseShape implements Shape.ShapeListener {
    private ConcurrentHashMap<Pair<Integer, Integer>, Shape> shapeIDMap = new ConcurrentHashMap<Pair<Integer, Integer>, Shape>();

    @Override
    public void draw(GL10 gl) {
        for (Shape shape : shapeIDMap.values()) {
            shape.draw(gl);
        }
    }

    public void addShape(Shape shape, int shapeID, int userID) {
        shapeIDMap.put(Pair.create(shapeID, userID), shape);
        shape.attachListener(this);
        invalide();
    }

    public void removeShape(Shape shapeToRemove) {
        List<Pair<Integer, Integer>> removeKeys = new ArrayList<Pair<Integer, Integer>>();

        for (Map.Entry<Pair<Integer, Integer>, Shape> pairShapeEntry : shapeIDMap.entrySet()) {
            if (pairShapeEntry.getValue().equals(shapeToRemove)) {
                removeKeys.add(pairShapeEntry.getKey());
            }
        }

        for (Pair<Integer, Integer> removeKey : removeKeys) {
            shapeIDMap.remove(removeKey);
        }

        invalide();
    }

    public void removeShape(int shapeID, int userID) {
        shapeIDMap.remove(Pair.create(shapeID, userID));
        invalide();
    }

    @Override
    public void onShapeChanged() {
        invalide();
    }
}
