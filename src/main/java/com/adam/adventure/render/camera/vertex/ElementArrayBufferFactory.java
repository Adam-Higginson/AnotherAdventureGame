package com.adam.adventure.render.camera.vertex;

import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.glGenBuffers;

public class ElementArrayBufferFactory {

    public ElementArrayBuffer newElementArrayBuffer(final int[] indices) {
        final int bufferId = glGenBuffers();
        final IntBuffer indicesBuffer = buildIndicesBuffer(indices);
        return new ElementArrayBuffer(bufferId, indicesBuffer, indices.length);
    }

    private IntBuffer buildIndicesBuffer(final int[] indices) {
        final IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        return indicesBuffer;
    }

}
