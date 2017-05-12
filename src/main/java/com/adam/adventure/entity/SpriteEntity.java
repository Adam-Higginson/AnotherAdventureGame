package com.adam.adventure.entity;

import com.adam.adventure.render.sprite.Sprite;

public class SpriteEntity extends Entity<SpriteEntity> {
    private final Sprite sprite;

    public SpriteEntity(final Sprite sprite) {
        super();
        this.sprite = sprite;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
