package com.adam.adventure.render.texture;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_R32I;
import static org.lwjgl.opengl.GL30.GL_RED_INTEGER;

public class TextureFactory {

    public Texture loadImageTextureFromFileNameInResources(final String fileName) throws IOException {
        try (final InputStream inputStream = this.getClass().getResourceAsStream(fileName)) {
            return loadTextureFromPng(inputStream);
        }
    }

    public Texture loadImageTextureFromFileName(final String fileName) throws IOException {
        try (final InputStream inputStream = new FileInputStream(fileName)) {
            return loadTextureFromPng(inputStream);
        }
    }


    public Texture loadTextureFromPng(final InputStream inputStream) throws IOException {
        final PNGDecoder pngDecoder = new PNGDecoder(inputStream);
        final ByteBuffer byteBuffer = createByteBufferFromPng(pngDecoder);

        final int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, pngDecoder.getWidth(), pngDecoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, byteBuffer);
        //Unbind texture
        glBindTexture(GL_TEXTURE_2D, 0);

        return new Texture(textureId, pngDecoder.getWidth(), pngDecoder.getHeight());
    }


    public Texture loadDataTexture(final int[] data, final int width, final int height) {
        final IntBuffer buffer = BufferUtils.createIntBuffer(width * height);
        for (final int value : data) {
            buffer.put(value);
        }

        buffer.flip();

        final int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_R32I, width, height, 0, GL_RED_INTEGER, GL11.GL_INT, buffer);
        //Unbind texture
        glBindTexture(GL_TEXTURE_2D, 0);

        return new Texture(textureId, width, height);
    }

    private ByteBuffer createByteBufferFromPng(final PNGDecoder pngDecoder) throws IOException {

        final ByteBuffer buffer = ByteBuffer.allocateDirect(4 * pngDecoder.getWidth() * pngDecoder.getHeight());
        pngDecoder.decodeFlipped(buffer, pngDecoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        buffer.flip();

        return buffer;
    }
}
