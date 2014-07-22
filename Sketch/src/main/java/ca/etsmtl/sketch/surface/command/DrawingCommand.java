package ca.etsmtl.sketch.surface.command;

import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.surface.openglshape.Drawing;

public interface DrawingCommand {
    int getUserIdSource();

    void execute(Drawing drawing, EventBus bus);

    void undo(Drawing drawing, EventBus bus);

    void redo(Drawing drawing, EventBus bus);
}
