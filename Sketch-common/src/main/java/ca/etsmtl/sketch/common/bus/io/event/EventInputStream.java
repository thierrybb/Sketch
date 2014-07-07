package ca.etsmtl.sketch.common.bus.io.event;

import ca.etsmtl.sketch.common.bus.event.Event;

public interface EventInputStream {
    Event readEvent() throws Exception;
}
