package ca.etsmtl.sketch.common.event;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.event.Event;
import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;


public class OnSyncDrawingEvent implements Event {
    @Override
    public void writeInto(DataOutputStream stream) throws IOException {

    }

    @Override
    public void readFrom(DataInputStream stream) throws IOException {

    }
}
