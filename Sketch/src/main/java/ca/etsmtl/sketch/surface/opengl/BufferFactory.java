package ca.etsmtl.sketch.surface.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class BufferFactory {
    // A texture is defined by X and Y coordinates
    private static final int TEXTURE_TYPE_NUMBER_OF_COORDINATE = 2;
    public static int FLOAT_TYPE_SIZE_IN_BYTE = 4;
    private static int SHORT_TYPE_SIZE_IN_BYTE = 2;

    public static FloatBuffer buildFloatBuffer(int size) {
        // a float is 4 bytes, therefore we multiply the number if
        // vertices with 4.
        ByteBuffer vbb = ByteBuffer.allocateDirect(size * FLOAT_TYPE_SIZE_IN_BYTE);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.position(0);
        return vertexBuffer;
    }

    public static FloatBuffer buildFloatBuffer(float[] vertices) {
        // a float is 4 bytes, therefore we multiply the number if
        // vertices with 4.
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * FLOAT_TYPE_SIZE_IN_BYTE);
        vbb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        return vertexBuffer;
    }

    public static ShortBuffer buildShortBuffer(short[] indices) {
        // short is 2 bytes, therefore we multiply the number if
        // vertices with 2.
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * SHORT_TYPE_SIZE_IN_BYTE);
        ibb.order(ByteOrder.nativeOrder());
        ShortBuffer indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);
        return indexBuffer;
    }

    public static FloatBuffer buildTextureBuffer(float[] textureCoordinates, int numberOfVertices) {
        ByteBuffer tbb = ByteBuffer.allocateDirect(numberOfVertices * TEXTURE_TYPE_NUMBER_OF_COORDINATE * FLOAT_TYPE_SIZE_IN_BYTE);
        tbb.order(ByteOrder.nativeOrder());
        FloatBuffer texBuffer = tbb.asFloatBuffer();
        texBuffer.put(textureCoordinates);
        texBuffer.position(0);
        return texBuffer;
    }

    public static FloatBuffer buildTextureBuffer2(float[] textureCoordinates, float[] vertices) {
        ByteBuffer tbb = ByteBuffer.allocateDirect(vertices.length * TEXTURE_TYPE_NUMBER_OF_COORDINATE * FLOAT_TYPE_SIZE_IN_BYTE);
        tbb.order(ByteOrder.nativeOrder());
        FloatBuffer texBuffer = tbb.asFloatBuffer();
        texBuffer.put(textureCoordinates);
        texBuffer.position(0);
        return texBuffer;
    }
}