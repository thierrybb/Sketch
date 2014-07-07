package ca.etsmtl.sketch.common.bus.io.ois;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.GZIPInputStream;

import ca.etsmtl.sketch.common.bus.io.DataInputStream;

public class DataInputStreamWrapper implements DataInputStream {
    private InputStream inputStream;

    public DataInputStreamWrapper(InputStream inputStream) throws IOException {
        this.inputStream = (inputStream);
    }

    @Override
    public String readString() throws IOException {
        int bytesLength = readInt();

        if (bytesLength < 0) {
            throw new IOException("Stream closed");
        }

        byte[] stringContent = read(inputStream, bytesLength);
        return new String(stringContent);
    }

    private static int readInt(byte[] byteArray) {
        return ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    private static float readFloat(byte[] byteArray) {
        return ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).getFloat();
    }

    private static double readDouble(byte[] byteArray) {
        return ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).getDouble();
    }

    private static byte[] read(InputStream inputStream, int count) throws IOException {
        byte[] bytesRead = new byte[count];
        inputStream.read(bytesRead);
        return bytesRead;
    }

    @Override
    public int readInt() throws IOException {
        return readInt(read(inputStream, 4));
    }

    @Override
    public double readDouble() throws IOException {
        return readDouble(read(inputStream, 8));
    }

    @Override
    public float readFloat() throws IOException {
        return readFloat(read(inputStream, 4));
    }
}
