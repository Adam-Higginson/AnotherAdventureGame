package com.adam.adventure.render.vertex;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;

public class VertexBufferFactory {

    public StaticVertexBuffer newStaticVertexBuffer(final Vertex[] vertices) {
        final FloatBuffer floatBuffer = buildFloatBuffer(vertices);

        final int bufferId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);

        return new StaticVertexBuffer(bufferId);
    }

    private FloatBuffer buildFloatBuffer(final Vertex[] vertices) {
        final float[] vertexData = Vertex.toArray(vertices);
        final FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertexData.length);
        verticesBuffer.put(vertexData);
        verticesBuffer.flip();

        return verticesBuffer;
    }
}
