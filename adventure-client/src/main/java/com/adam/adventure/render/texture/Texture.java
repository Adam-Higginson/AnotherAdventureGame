package com.adam.adventure.render.texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Texture {
    private final int textureId;
    private final int width;
    private final int height;

    Texture(final int textureId, final int width, final int height) {
        this.textureId = textureId;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void bindTexture() {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public void bindTexture(final int activeTexture) {
        glActiveTexture(GL_TEXTURE0 + activeTexture);
    }
}
