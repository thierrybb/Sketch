package ca.etsmtl.sketch.common.bus.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

public class OnRequestUnregisterToEventType implements Event {
    private Class<? extends Event> eventClassToUnregister;

    public OnRequestUnregisterToEventType() {
    }

    public OnRequestUnregisterToEventType(Class<? extends Event> eventClassToUnregister) {
        this.eventClassToUnregister = eventClassToUnregister;
    }

    public Class<? extends Event> getEventClassToUnregister() {
        return eventClassToUnregister;
    }

    @Override
    public void writeInto(DataOutputStream stream) throws IOException {
        stream.writeString(eventClassToUnregister.getName());
    }

    @Override
    public void readFrom(DataInputStream stream) throws IOException {
        try {
            this.eventClassToUnregister = (Class<? extends Event>) Class.forName(stream.readString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
