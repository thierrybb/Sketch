package ca.etsmtl.sketch.surface.shape;


abstract class BaseShape implements Shape {
    private ShapeListener listener;

    @Override
    public void attachListener(ShapeListener listener) {
        this.listener = listener;
    }

    protected void invalide() {
        listener.onShapeChanged();
    }
}
