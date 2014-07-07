package ca.etsmtl.sketch.common.bus.io;

import java.io.IOException;

public interface DataInputStream {
    String readString() throws IOException;

    int readInt() throws IOException;

    double readDouble() throws IOException;

    float readFloat() throws IOException;
}
