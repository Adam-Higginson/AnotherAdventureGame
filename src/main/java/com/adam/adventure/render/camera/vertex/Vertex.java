package com.adam.adventure.render.camera.vertex;

public class Vertex {
    public static final int NUM_ELEMENTS_PER_VERTEX = 3;

    private final float x;
    private final float y;
    private final float z;

    public Vertex(final float x, final float y) {
        this(x, y, 0.0f);
    }

    Vertex(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vertex of(final float x, final float y) {
        return new Vertex(x, y);
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

    public static float[] toArray(final Vertex[] vertices) {
        final float[] vertexDataArray = new float[vertices.length * NUM_ELEMENTS_PER_VERTEX];
        int currentOffset = 0;
        for (int i = 0; i < vertices.length; i++) {
            final Vertex vertex = vertices[i];
            vertexDataArray[currentOffset++] = vertex.getX();
            vertexDataArray[currentOffset++] = vertex.getY();
            vertexDataArray[currentOffset++] = vertex.getZ();
        }

        return vertexDataArray;
    }
}
