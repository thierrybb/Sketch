package ca.etsmtl.sketch.surface.shape;

import java.util.ArrayList;
import java.util.List;

import ca.etsmtl.sketch.surface.graphic.Graphics;

public class Drawing implements Shape, Shape.ShapeListener {
    private List<Shape> shapes = new ArrayList<Shape>();
    private ShapeListener listener = ShapeListener.NULL_LISTENER;

    public void addShape(Shape shape) {
        shapes.add(shape);
        shape.attachListener(this);
        listener.onShapeChanged();
    }

    public void removeShape(Shape shape) {
        if (shapes.remove(shape)) {
            listener.onShapeChanged();
        }
    }

    @Override
    public void draw(Graphics graphics) {
        for (Shape shape : shapes) {
            shape.draw(graphics);
        }
    }

    @Override
    public void attachListener(ShapeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onShapeChanged() {
        listener.onShapeChanged();
    }
}
