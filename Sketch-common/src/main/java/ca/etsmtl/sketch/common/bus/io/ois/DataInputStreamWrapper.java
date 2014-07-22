package ca.etsmtl.sketch.common.bus.io.ois;

import java.io.IOException;
import java.io.InputStream;

import ca.etsmtl.sketch.common.bus.io.DataInputStream;

public class DataInputStreamWrapper implements DataInputStream {
    private java.io.DataInputStream inputStream;

    public DataInputStreamWrapper(InputStream inputStream) throws IOException {
        this.inputStream = new java.io.DataInputStream(inputStream);
    }

    @Override
    public String readString() throws IOException {
        return inputStream.readUTF();
    }

    @Override
    public int readInt() throws IOException {
        return inputStream.readInt();
    }

    @Override
    public double readDouble() throws IOException {
        return inputStream.readDouble();
    }

    @Override
    public float readFloat() throws IOException {
        return inputStream.readFloat();
    }
}
