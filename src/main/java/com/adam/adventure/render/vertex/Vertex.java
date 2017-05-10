package com.adam.adventure.render.vertex;

public class Vertex {
    private static final int FLOAT_BYTES = 4;

    /**
     * How many components make up the vertex
     */
    public static final int NUM_ELEMENTS_PER_VERTEX = 5;

    /**
     * How many bytes into a vertex do the texture coordinates begin?
     */
    public static final int TEXTURE_OFFSET = 3 * FLOAT_BYTES;

    /**
     * How many bytes per vertex
     */
    public static final int STRIDE = NUM_ELEMENTS_PER_VERTEX * FLOAT_BYTES;

    private final float x;
    private final float y;
    private final float z;
    private final float s;
    private final float t;

    public Vertex(final float x, final float y) {
        this(x, y, 0.0f);
    }

    Vertex(final float x, final float y, final float z) {

        this(x, y, z, 0.0f, 0.0f);
    }

    public Vertex(final float x, final float y, final float z, final float s, final float t) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.s = s;
        this.t = t;
    }

    public static Vertex of(final float x, final float y) {
        return new Vertex(x, y);
    }

    public static Vertex of(final float x, final float y, final float s, final float t) {
        return new Vertex(x, y, 0.0f, s, t);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getS() {
        return s;
    }

    public float getT() {
        return t;
    }

    public static float[] toArray(final Vertex[] vertices) {
        final float[] vertexDataArray = new float[vertices.length * NUM_ELEMENTS_PER_VERTEX];
        int offset = 0;
        for (int i = 0; i < vertices.length; i++) {
            final Vertex vertex = vertices[i];
            vertexDataArray[offset++] = vertex.getX();
            vertexDataArray[offset++] = vertex.getY();
            vertexDataArray[offset++] = vertex.getZ();
            vertexDataArray[offset++] = vertex.getS();
            vertexDataArray[offset++] = vertex.getT();
        }

        return vertexDataArray;
    }
}
