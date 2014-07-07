package ca.etsmtl.sketch.surface.openglshape;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.microedition.khronos.opengles.GL10;

public class Drawing extends BaseShape implements Shape.ShapeListener {
    private List<Shape> shapes = new CopyOnWriteArrayList<Shape>();

    @Override
    public void draw(GL10 gl) {
        for (Shape shape : shapes) {
            shape.draw(gl);
        }
    }

    public void addShape(Shape shape) {
        shapes.add(shape);
        shape.attachListener(this);
        invalide();
    }

    public void removeShape(Shape shape) {
        if (shapes.remove(shape)) {
            invalide();
        }
    }

    @Override
    public void onShapeChanged() {
        invalide();
    }
}
