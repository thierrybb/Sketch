package ca.etsmtl.sketch.surface.command;

import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.event.OnInkStrokeReAdded;
import ca.etsmtl.sketch.surface.openglshape.Drawing;
import ca.etsmtl.sketch.surface.openglshape.InkStroke;
import ca.etsmtl.sketch.surface.openglshape.Shape;

public class EraseInkStroke implements  DrawingCommand {
    private int eraserUserID;
    private int shapeUserID;
    private int shapeID;

    private Shape removedShape;

    public EraseInkStroke(int eraserUserID, int shapeUserID, int shapeID) {
        this.eraserUserID = eraserUserID;
        this.shapeUserID = shapeUserID;
        this.shapeID = shapeID;
    }

    @Override
    public int getUserIdSource() {
        return eraserUserID;
    }

    @Override
    public void execute(Drawing drawing, EventBus bus) {
        removedShape = drawing.shapeByID(shapeUserID, shapeID);
        drawing.removeShape(removedShape);
    }

    @Override
    public void undo(Drawing drawing, EventBus bus) {
        // TODO : Refactor... pas bon pentoute
        if (removedShape instanceof InkStroke) {
            InkStroke strokes = (InkStroke) removedShape;
            bus.post(new OnInkStrokeReAdded(strokes.getPoints(), strokes.getStrokeColor(),
                    shapeUserID, shapeID));
        }
    }

    @Override
    public void redo(Drawing drawing, EventBus bus) {
        execute(drawing, bus);
    }
}
