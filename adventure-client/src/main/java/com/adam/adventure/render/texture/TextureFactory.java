package com.adam.adventure.render.texture;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class TextureFactory {

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
        glBindTexture(GL_TEXTURE_2D, 0);

        return new Texture(textureId, pngDecoder.getWidth(), pngDecoder.getHeight());
    }


    private ByteBuffer createByteBufferFromPng(final PNGDecoder pngDecoder) throws IOException {

        final ByteBuffer buffer = ByteBuffer.allocateDirect(4 * pngDecoder.getWidth() * pngDecoder.getHeight());
        pngDecoder.decodeFlipped(buffer, pngDecoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        buffer.flip();


        return buffer;
    }
}
