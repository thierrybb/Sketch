package ca.etsmtl.sketch.common.bus.io;

import java.io.IOException;

public interface DataOutputStream {
    void writeString(String value) throws IOException;

    void writeInt(Integer value) throws IOException;

    void writeDouble(Double value) throws IOException;

    void writeFloat(Float value) throws IOException;

    void flush() throws IOException;
}
