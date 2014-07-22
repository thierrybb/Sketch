package ca.etsmtl.sketch.surface.touch;

import android.content.Context;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import ca.etsmtl.sketch.common.graphic.PointF;
import ca.etsmtl.sketch.surface.transformation.MatrixWrapper;

public class FingerMotionMatrixDelegator extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

    private PointF middlePoint = new PointF(0, 0);
    private PointF zoomStartMiddle = new PointF(0, 0);
    private float startDistance;
    private PointF dragStartPoint = new PointF(0, 0);
    private PointF lastDragPoint = null;

    private MatrixWrapper matrix;

    private boolean isZooming;
    private boolean isDragging;
    private boolean hasDragged = false;
    private GestureDetector gestureDetector;
    private float lastExpectedScale;

    public FingerMotionMatrixDelegator(Context context, MatrixWrapper wrapper) {
        this.matrix = wrapper;
        gestureDetector = new GestureDetector(context, this);
//        continueScroll = new ContinueScroll(this, context);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return processTouchEvent(event);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        if (lastDragPoint != null) {
//            continueScroll.init((int) velocityX, (int) velocityY, lastDragPoint.x, lastDragPoint.y);
//            context.post(continueScroll);
//            lastDragPoint = null;
//        }

        return super.onFling(e1, e2, velocityX, velocityY);
    }

    public void postScale(float expectedScale) {
        if (expectedScale == lastExpectedScale)
            return;

        lastExpectedScale = expectedScale;

        matrix.restore();
        matrix.postScale(expectedScale, middlePoint);
        matrix.postTranslation(middlePoint.x - zoomStartMiddle.x,
                middlePoint.y - zoomStartMiddle.y);
    }

    public boolean processTouchEvent(MotionEvent event) {

        if (event == null)
            return false;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                processPointerDown(event);
                break;
            case MotionEvent.ACTION_DOWN:
                processActionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                processPointerMove(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                processPointerUp(event);
                break;
        }

        gestureDetector.onTouchEvent(event);
        return isZooming || isDragging;
    }

    private void processActionDown(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            dragStartPoint.x = event.getX();
            dragStartPoint.y = event.getY();
            lastDragPoint = new PointF(0, 0);
            isDragging = true;
            hasDragged = false;
        }
    }

    private void processPointerDown(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            startDistance = spacing(event);
            if (startDistance > 10f) {
                midPoint(middlePoint, event);
                midPoint(zoomStartMiddle, event);
                isZooming = true;
                lastDragPoint = null;
                isDragging = false;
                matrix.save();
            }
        }
    }

    private void processPointerMove(MotionEvent event) {
        if (isZooming) {
            float newDist = spacing(event);
            if (newDist > 10f) {
                float scale = newDist / startDistance;
                midPoint(middlePoint, event);
                postScale(scale);
            }
        } else if (isDragging) {
            if (!hasDragged) {
                matrix.save();
            }

            lastDragPoint.x = event.getX();
            lastDragPoint.y = event.getY();

            matrix.restore();
            matrix.postTranslation(event.getX() - dragStartPoint.x,
                    event.getY() - dragStartPoint.y);
            hasDragged = true;
        }

    }

    private void processPointerUp(MotionEvent event) {
        isZooming = false;
        isDragging = false;
    }

    private PointF midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
        return point;
    }

    private float spacing(MotionEvent event) {
        if (event.getPointerCount() < 2)
            return 0.0f;

        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        matrix.postScale(1.5f, new PointF(e.getX(), e.getY()));
        return super.onDoubleTap(e);
    }
}
