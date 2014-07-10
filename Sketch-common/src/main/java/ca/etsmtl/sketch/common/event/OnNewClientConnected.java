package ca.etsmtl.sketch.common.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

public class OnNewClientConnected implements Event {
    private String name;
    private int id;

    public OnNewClientConnected(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public OnNewClientConnected() {
    }

    public String getName() {
        return name;
    }

    public int getUserId() {
        return id;
    }

    @Override
    public void writeInto(DataOutputStream stream) throws IOException {
        stream.writeString(name);
        stream.writeInt(id);
    }

    @Override
    public void readFrom(DataInputStream stream) throws IOException {
        name = stream.readString();
        id = stream.readInt();
    }
}
