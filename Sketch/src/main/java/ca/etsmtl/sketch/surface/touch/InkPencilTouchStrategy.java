package ca.etsmtl.sketch.surface.touch;

import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.event.OnFingerDownInkMode;
import ca.etsmtl.sketch.common.event.OnFingerMoveInkMode;
import ca.etsmtl.sketch.common.event.OnFingerUpInkMode;
import ca.etsmtl.sketch.common.graphic.PointF;

public class InkPencilTouchStrategy implements TouchStrategy {
    private int userID;
    private EventBus eventBus;

    public InkPencilTouchStrategy(int userID, EventBus eventBus) {
        this.userID = userID;
        this.eventBus = eventBus;
    }

    @Override
    public void onPointerDown(int pointerID, PointF position) {
        eventBus.post(new OnFingerDownInkMode(pointerID, userID, position));
    }

    @Override
    public void onPointerUp(int pointerID) {
        eventBus.post(new OnFingerUpInkMode(pointerID, userID));
    }

    @Override
    public void onPointerMove(int pointerID, PointF newPosition) {
        eventBus.post(new OnFingerMoveInkMode(pointerID, newPosition, userID));
    }
}
