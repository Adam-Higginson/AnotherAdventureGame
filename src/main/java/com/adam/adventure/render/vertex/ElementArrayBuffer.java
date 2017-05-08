package com.adam.adventure.render.vertex;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;

public class ElementArrayBuffer implements Buffer {
    private final int bufferId;
    private final IntBuffer intBuffer;
    private final int numberOfIndices;

    public ElementArrayBuffer(final int bufferId, final IntBuffer intBuffer, final int numberOfIndices) {
        this.bufferId = bufferId;
        this.intBuffer = intBuffer;
        this.numberOfIndices = numberOfIndices;
    }

    @Override
    public void bindBufferData() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL_STATIC_DRAW);
    }

    @Override
    public int getNumberOfElements() {
        return numberOfIndices;
    }

}
