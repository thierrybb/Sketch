package ca.etsmtl.sketch.common.bus.io.ois;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.GZIPOutputStream;

import ca.etsmtl.sketch.common.bus.io.DataOutputStream;

public class DataOutputStreamWrapper implements DataOutputStream {
    private OutputStream outputStream;

    public DataOutputStreamWrapper(OutputStream out) {
            outputStream = new BufferedOutputStream(out);
    }

    public static byte[] convertFloatByteArray(float value) {
        return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putFloat(value).array();
    }

    public static byte[] convertDoubleByteArray(double value) {
        return ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putDouble(value).array();
    }

    public static byte[] convertIntToByteArray(int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(value).array();
    }

    @Override
    public void writeString(String value) throws IOException {
        byte[] bytes = value.getBytes();
        writeInt(bytes.length);
        outputStream.write(bytes);
    }

    @Override
    public void writeInt(Integer value) throws IOException {
        outputStream.write(convertIntToByteArray(value));
    }

    @Override
    public void writeDouble(Double value) throws IOException {
        outputStream.write(convertDoubleByteArray(value));
    }

    @Override
    public void writeFloat(Float value) throws IOException {
        outputStream.write(convertFloatByteArray(value));
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }
}
