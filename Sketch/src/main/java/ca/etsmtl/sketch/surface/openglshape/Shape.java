package ca.etsmtl.sketch.surface.openglshape;

import javax.microedition.khronos.opengles.GL10;

public interface Shape {
    public interface ShapeListener {
        void onShapeChanged();

        public static ShapeListener NULL_LISTENER = new ShapeListener() {
            @Override
            public void onShapeChanged() {

            }
        };
    }

    void draw(GL10 gl);

    public void attachListener(ShapeListener listener);
}
