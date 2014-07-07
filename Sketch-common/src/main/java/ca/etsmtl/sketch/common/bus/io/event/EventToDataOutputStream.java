package ca.etsmtl.sketch.common.bus.io.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

public class EventToDataOutputStream implements EventOutputStream {
    private DataOutputStream outputStream;

    public EventToDataOutputStream(DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void writeEvent(Event event) throws IOException {
        outputStream.writeString(event.getClass().getName());
        event.writeInto(outputStream);
        outputStream.flush();
    }
}
