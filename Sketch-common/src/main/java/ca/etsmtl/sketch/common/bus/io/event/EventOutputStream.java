package ca.etsmtl.sketch.common.bus.io.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.event.Event;

public interface EventOutputStream {
    void writeEvent(Event event) throws IOException;
}
