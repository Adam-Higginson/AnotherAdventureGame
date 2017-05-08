package com.adam.adventure.render.camera.vertex;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.glGenBuffers;

public class VertexBufferFactory {

    public StaticVertexBuffer newStaticVertexBuffer(final Vertex[] vertices) {
        final FloatBuffer floatBuffer = buildFloatBuffer(vertices);
        final int bufferId = glGenBuffers();
        return new StaticVertexBuffer(bufferId, floatBuffer, vertices.length);

    }

    private FloatBuffer buildFloatBuffer(final Vertex[] vertices) {
        final float[] vertexData = Vertex.toArray(vertices);
        final FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertexData.length);
        verticesBuffer.put(vertexData);
        verticesBuffer.flip();

        return verticesBuffer;
    }
}
