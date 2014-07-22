package ca.etsmtl.sketch.surface.touch;

import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;

import ca.etsmtl.sketch.common.graphic.PointF;
import ca.etsmtl.sketch.surface.transformation.MatrixWrapper;

public class StrategyTouchListenerDelegator implements View.OnTouchListener {

    private TouchStrategy currentStrategy;
    private MatrixWrapper pointMatrix;

    private HashMap<Integer, PointF> fingerLastPos = new HashMap<Integer, PointF>();

    public StrategyTouchListenerDelegator(MatrixWrapper pointMatrix) {
        this.currentStrategy = TouchStrategy.NULL_STRATEGY;
        this.pointMatrix = pointMatrix;
    }

    public void setCurrentStrategy(TouchStrategy currentStrategy) {
        this.currentStrategy = currentStrategy;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int pointerId = event.getPointerId(event.getActionIndex());

        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN
                || event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            PointF point = new PointF(event.getX(event.getActionIndex()), event.getY(event.getActionIndex()));
            fingerLastPos.put(pointerId, point);
            point = pointMatrix.computePointFromInverse(point);
            currentStrategy.onPointerDown(pointerId, point);
        } else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP
                || event.getActionMasked() == MotionEvent.ACTION_UP
                || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            currentStrategy.onPointerUp(pointerId);
        } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            for (int i = 0; i < event.getPointerCount(); i++) {
                pointerId = event.getPointerId(i);

                if (fingerLastPos.containsKey(pointerId)) {
                    PointF lastPos = fingerLastPos.get(pointerId);

                    if (!lastPos.equals(event.getX(i), event.getY(i))) {
                        PointF newPoint = new PointF(event.getX(i), event.getY(i));
                        newPoint = pointMatrix.computePointFromInverse(newPoint);
                        currentStrategy.onPointerMove(pointerId, newPoint);
                    }
                }
            }
        }

        return true;
    }
}
