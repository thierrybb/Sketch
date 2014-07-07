package ca.etsmtl.sketch.surface.touch;

import android.util.Pair;

import java.util.HashMap;

import ca.etsmtl.sketch.common.bus.component.UniqueIDGenerator;
import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.bus.eventbus.Subscribe;
import ca.etsmtl.sketch.common.event.OnFingerDownInkMode;
import ca.etsmtl.sketch.common.event.OnFingerMoveInkMode;
import ca.etsmtl.sketch.common.event.OnFingerUpInkMode;
import ca.etsmtl.sketch.common.event.OnInkStrokeAdded;
import ca.etsmtl.sketch.surface.openglshape.Drawing;
import ca.etsmtl.sketch.surface.openglshape.FingerCursorShape;

public class OpenGLInkModeTouchComponent {
    private Drawing drawing;

    private HashMap<Pair<Integer, Integer>, FingerCursorShape> allShapesForFinger = new HashMap<Pair<Integer, Integer>, FingerCursorShape>();
    private EventBus bus;
    private int strokeColor = 0;
    private int currentUserID;
    private UniqueIDGenerator newShapeIDGenerator;

    public OpenGLInkModeTouchComponent(Drawing drawing, int currentUserID, UniqueIDGenerator newShapeIDGenerator) {
        this.drawing = drawing;
        this.currentUserID = currentUserID;
        this.newShapeIDGenerator = newShapeIDGenerator;
    }

    public void plug(EventBus bus) throws NoSuchMethodException {
        bus.register(this, OnFingerDownInkMode.class);
        bus.register(this, OnFingerUpInkMode.class);
        bus.register(this, OnFingerMoveInkMode.class);
        this.bus = bus;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    @Subscribe
    public void onFingerDown(OnFingerDownInkMode event) {
        FingerCursorShape newShape = new FingerCursorShape();
        newShape.accumulatePoint(event.getPosition());
        allShapesForFinger.put(Pair.create(event.getUserID(), event.getFingerID()), newShape);
        drawing.addShape(newShape, newShapeIDGenerator.generateUniqueID(), currentUserID);
    }

    @Subscribe
    public void onFingerMove(OnFingerMoveInkMode event) {
        Pair<Integer, Integer> key = Pair.create(event.getUserID(), event.getFingerID());
        if (allShapesForFinger.containsKey(key)) {
            FingerCursorShape fingerCursorShape = allShapesForFinger.get(key);
            fingerCursorShape.accumulatePoint(event.getPosition());
        }
    }

    @Subscribe
    public void onFingerUp(OnFingerUpInkMode event) {
        Pair<Integer, Integer> key = Pair.create(event.getUserID(), event.getFingerID());

        if (allShapesForFinger.containsKey(key)) {
            FingerCursorShape fingerCursorShape = allShapesForFinger.get(key);
            drawing.removeShape(fingerCursorShape);
            allShapesForFinger.remove(key);

            if (event.getUserID() == currentUserID) {
                bus.post(new OnInkStrokeAdded(fingerCursorShape.getPoints(), strokeColor, currentUserID, newShapeIDGenerator.generateUniqueID()));
            }
        }
    }
}
