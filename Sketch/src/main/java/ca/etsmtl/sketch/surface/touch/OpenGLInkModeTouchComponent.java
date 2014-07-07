package ca.etsmtl.sketch.surface.touch;

import java.util.HashMap;

import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.bus.eventbus.Subscribe;
import ca.etsmtl.sketch.common.event.OnFingerDownInkMode;
import ca.etsmtl.sketch.common.event.OnFingerMoveInkMode;
import ca.etsmtl.sketch.common.event.OnFingerUpInkMode;
import ca.etsmtl.sketch.common.event.OnInkDrawingAdded;
import ca.etsmtl.sketch.surface.openglshape.Drawing;
import ca.etsmtl.sketch.surface.openglshape.FingerCursorShape;

public class OpenGLInkModeTouchComponent {
    private Drawing drawing;

    private HashMap<Integer, FingerCursorShape> allShapesForFinger = new HashMap<Integer, FingerCursorShape>();
    private EventBus bus;

    public OpenGLInkModeTouchComponent(Drawing drawing) {
        this.drawing = drawing;
    }

    public void plug(EventBus bus) throws NoSuchMethodException {
        bus.register(this, OnFingerDownInkMode.class);
        bus.register(this, OnFingerUpInkMode.class);
        bus.register(this, OnFingerMoveInkMode.class);
        this.bus = bus;
    }

    @Subscribe
    public void onFingerDown(OnFingerDownInkMode event) {
        FingerCursorShape newShape = new FingerCursorShape();
        newShape.accumulatePoint(event.getPosition());
        allShapesForFinger.put(event.getFingerID(), newShape);
        drawing.addShape(newShape);
    }

    @Subscribe
    public void onFingerMove(OnFingerMoveInkMode event) {
        if (allShapesForFinger.containsKey(event.getFingerID())) {
            FingerCursorShape fingerCursorShape = allShapesForFinger.get(event.getFingerID());
            fingerCursorShape.accumulatePoint(event.getPosition());
        }
    }

    @Subscribe
    public void onFingerUp(OnFingerUpInkMode event) {
        if (allShapesForFinger.containsKey(event.getFingerID())) {
            FingerCursorShape fingerCursorShape = allShapesForFinger.get(event.getFingerID());
            drawing.removeShape(fingerCursorShape);
            allShapesForFinger.remove(event.getFingerID());

            bus.post(new OnInkDrawingAdded(fingerCursorShape.getPoints()));
        }
    }
}
