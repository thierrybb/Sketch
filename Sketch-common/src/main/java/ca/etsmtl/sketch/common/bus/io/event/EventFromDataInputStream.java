package ca.etsmtl.sketch.common.bus.io.event;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.io.DataInputStream;

public class EventFromDataInputStream implements EventInputStream {
    private DataInputStream inputStream;

    public EventFromDataInputStream(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public Event readEvent() throws Exception {
        String classType = inputStream.readString();

        Class<? extends Event> eventClassName = (Class<? extends Event>) Class.forName(classType);
        Event event = eventClassName.newInstance();
        event.readFrom(inputStream);
        return event;
    }
}
