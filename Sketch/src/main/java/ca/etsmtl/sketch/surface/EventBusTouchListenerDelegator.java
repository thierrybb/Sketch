package ca.etsmtl.sketch.surface;

import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;

import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.event.OnFingerDownInkMode;
import ca.etsmtl.sketch.common.event.OnFingerMoveInkMode;
import ca.etsmtl.sketch.common.event.OnFingerUpInkMode;
import ca.etsmtl.sketch.common.graphic.PointF;

public class EventBusTouchListenerDelegator implements View.OnTouchListener {
    private EventBus eventBus;

    private HashMap<Integer, PointF> fingerLastPos = new HashMap<Integer, PointF>();

    public EventBusTouchListenerDelegator(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int pointerId = event.getPointerId(event.getActionIndex());

        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN
                || event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            PointF point = new PointF(event.getX(event.getActionIndex()), event.getY(event.getActionIndex()));
            fingerLastPos.put(pointerId, point);
            eventBus.post(new OnFingerDownInkMode(pointerId, point));
        } else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP
                || event.getActionMasked() == MotionEvent.ACTION_UP
                || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            eventBus.post(new OnFingerUpInkMode(pointerId));
        } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            for (int i = 0; i < event.getPointerCount(); i++) {
                pointerId = event.getPointerId(i);

                if (fingerLastPos.containsKey(pointerId)) {
                    PointF lastPos = fingerLastPos.get(pointerId);

                    if (!lastPos.equals(event.getX(i), event.getY(i))) {
                        PointF newPoint = new PointF(event.getX(i), event.getY(i));
                        eventBus.post(new OnFingerMoveInkMode(pointerId, newPoint));
                    }
                }
            }
        }

        return true;
    }
}
