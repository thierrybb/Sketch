package ca.etsmtl.sketch.common.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

public class OnNewUserAdded implements Event {
    private String name;
    private int id;

    public OnNewUserAdded(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public OnNewUserAdded() {
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public void writeInto(DataOutputStream stream) throws IOException {
        stream.writeString(name);
    }

    @Override
    public void readFrom(DataInputStream stream) throws IOException {
        name = stream.readString();
    }
}
