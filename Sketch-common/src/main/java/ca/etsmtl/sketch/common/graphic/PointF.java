package ca.etsmtl.sketch.common.graphic;

public class PointF {
    public float x;
    public float y;

    public PointF(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean equals(float x, float y) {
        return this.x == x && this.y == y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
