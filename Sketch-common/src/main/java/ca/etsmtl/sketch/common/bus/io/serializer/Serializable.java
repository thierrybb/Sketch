package ca.etsmtl.sketch.common.bus.io.serializer;

import java.io.IOException;

import ca.etsmtl.sketch.common.bus.io.DataInputStream;
import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

public interface Serializable {
    void writeInto(DataOutputStream stream) throws IOException;

    void readFrom(DataInputStream stream) throws IOException;
}
