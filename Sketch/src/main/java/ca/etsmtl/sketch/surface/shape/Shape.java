package ca.etsmtl.sketch.surface.shape;

import ca.etsmtl.sketch.surface.graphic.Graphics;

public interface Shape {
    public interface ShapeListener {
        void onShapeChanged();

        public static ShapeListener NULL_LISTENER = new ShapeListener() {
            @Override
            public void onShapeChanged() {

            }
        };
    }

    void draw(Graphics graphics);

    public void attachListener(ShapeListener listener);
}
