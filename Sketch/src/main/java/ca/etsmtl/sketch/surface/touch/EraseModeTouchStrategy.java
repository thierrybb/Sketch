package ca.etsmtl.sketch.surface.touch;

import java.util.Map;

import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.event.OnInkStrokeErased;
import ca.etsmtl.sketch.common.graphic.PointF;
import ca.etsmtl.sketch.surface.openglshape.Drawing;
import ca.etsmtl.sketch.surface.openglshape.Shape;
import ca.etsmtl.sketch.common.utils.Identifier;

public class EraseModeTouchStrategy implements TouchStrategy {
    private PointF lastPointerPos = null;

    private int currentUserID;
    private Drawing drawing;
    private EventBus bus;

    public EraseModeTouchStrategy(int currentUserID, Drawing drawing, EventBus bus) {
        this.currentUserID = currentUserID;
        this.drawing = drawing;
        this.bus = bus;
    }

    @Override
    public void onPointerDown(int pointerID, PointF position) {

    }

    @Override
    public void onPointerUp(int pointerID) {
        lastPointerPos = null;
    }

    @Override
    public void onPointerMove(int pointerID, PointF newPosition) {
        if (lastPointerPos != null) {
            for (Map.Entry<Identifier, Shape> identifierShapeEntry : drawing) {
                if (identifierShapeEntry.getValue().intersect(lastPointerPos, newPosition)) {
                    Identifier identifier = identifierShapeEntry.getKey();
                    bus.post(new OnInkStrokeErased(identifier.getUserID(),
                            identifier.getLocalID(), currentUserID));
                }
            }
        }

        lastPointerPos = newPosition;
    }
}
