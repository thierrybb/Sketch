package ca.etsmtl.sketch.common.bus.io.ois;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.GZIPOutputStream;

import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

public class DataOutputStreamWrapper implements DataOutputStream {
    private java.io.DataOutputStream outputStream;

    public DataOutputStreamWrapper(OutputStream out) {
            outputStream = new java.io.DataOutputStream(out);
    }

    @Override
    public void writeString(String value) throws IOException {
        outputStream.writeUTF(value);
    }

    @Override
    public void writeInt(Integer value) throws IOException {
        outputStream.writeInt(value);
    }

    @Override
    public void writeDouble(Double value) throws IOException {
        outputStream.writeDouble(value);
    }

    @Override
    public void writeFloat(Float value) throws IOException {
        outputStream.writeFloat(value);
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }
}
