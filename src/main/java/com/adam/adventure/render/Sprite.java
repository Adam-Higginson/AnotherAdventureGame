package com.adam.adventure.render;

import com.adam.adventure.render.texture.Texture;
import com.adam.adventure.render.util.Rectangle;

public class Sprite {
    private final Texture texture;
    private Rectangle textureOffset;
    private final float width;
    private final float height;

    public Sprite(final Texture texture, final Rectangle textureOffset, final float width, final float height) {
        this.texture = texture;
        this.textureOffset = textureOffset;
        this.width = width;
        this.height = height;
    }

    public Texture getTexture() {
        return texture;
    }

    public Rectangle getTextureOffset() {
        return textureOffset;
    }

    public void setTextureOffset(final Rectangle textureOffset) {
        this.textureOffset = textureOffset;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
