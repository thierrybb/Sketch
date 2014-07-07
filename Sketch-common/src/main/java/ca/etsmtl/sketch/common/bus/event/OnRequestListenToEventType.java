package ca.etsmtl.sketch.common.bus.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

public class OnRequestListenToEventType implements Event {
    private Class<? extends Event> eventClassToListen;

    public OnRequestListenToEventType() {
    }

    public OnRequestListenToEventType(Class<? extends Event> eventClassToListen) {
        this.eventClassToListen = eventClassToListen;
    }

    public Class<? extends Event> getEventClassToListen() {
        return eventClassToListen;
    }

    @Override
    public void writeInto(DataOutputStream stream) throws IOException {
        stream.writeString(eventClassToListen.getName());
    }

    @Override
    public void readFrom(DataInputStream stream) throws IOException {
        try {
            this.eventClassToListen = (Class<? extends Event>) Class.forName(stream.readString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
