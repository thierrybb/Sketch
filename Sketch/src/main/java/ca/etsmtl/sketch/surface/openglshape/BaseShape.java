package ca.etsmtl.sketch.surface.openglshape;

public abstract class BaseShape implements Shape {
    private ShapeListener listener = ShapeListener.NULL_LISTENER;

    @Override
    public void attachListener(ShapeListener listener) {
        this.listener = listener;
    }

    protected void invalide() {
        listener.onShapeChanged();
    }
}
