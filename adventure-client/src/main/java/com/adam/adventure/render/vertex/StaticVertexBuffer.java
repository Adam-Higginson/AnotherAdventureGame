package com.adam.adventure.render.vertex;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;

public class StaticVertexBuffer implements Buffer {

    private final int bufferId;
    private final FloatBuffer floatBuffer;
    private final int numberOfVertices;

    StaticVertexBuffer(final int bufferId, final FloatBuffer floatBuffer, final int numberOfVertices) {
        this.bufferId = bufferId;
        this.floatBuffer = floatBuffer;
        this.numberOfVertices = numberOfVertices;
    }

    @Override
    public void bindBufferData() {
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
    }

    @Override
    public int getNumberOfElements() {
        return numberOfVertices;
    }
}
