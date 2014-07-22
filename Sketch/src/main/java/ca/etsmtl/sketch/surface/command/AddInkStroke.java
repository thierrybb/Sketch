package ca.etsmtl.sketch.surface.command;

import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.event.OnInkStrokeReAdded;
import ca.etsmtl.sketch.common.event.OnInkStrokeRemoved;
import ca.etsmtl.sketch.surface.openglshape.Drawing;
import ca.etsmtl.sketch.surface.openglshape.InkStroke;

public class AddInkStroke implements DrawingCommand {
    private final float[] points;
    private final int color;
    private final int userID;
    private final int shapeID;

    public AddInkStroke(float[] points, int color, int userID, int shapeID) {
        this.points = points;
        this.color = color;
        this.userID = userID;
        this.shapeID = shapeID;
    }

    @Override
    public int getUserIdSource() {
        return userID;
    }

    @Override
    public void execute(Drawing drawing, EventBus bus) {
        drawing.addShape(new InkStroke(points, color), shapeID, userID);
    }

    @Override
    public void undo(Drawing drawing, EventBus bus) {
        bus.post(new OnInkStrokeRemoved(userID, shapeID));
    }

    @Override
    public void redo(Drawing drawing, EventBus bus) {
        bus.post(new OnInkStrokeReAdded(points, color, userID, shapeID));
    }
}
