package com.adam.adventure.entity.component;

import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.render.renderable.Renderable;
import com.adam.adventure.render.renderable.SpriteRenderable;
import com.adam.adventure.render.sprite.Sprite;

import javax.inject.Inject;

public class SpriteRendererComponent extends EntityComponent {

    @Inject
    private RenderQueue renderQueue;

    private final Sprite sprite;

    private Renderable spriteRenderable;

    public SpriteRendererComponent(final Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void activate() {
        spriteRenderable = new SpriteRenderable(getEntity(), sprite, 0);
    }

    @Override
    protected void update(final float deltaTime) {
        renderQueue.addRenderable(spriteRenderable);
    }

    @Override
    protected void destroy() {
        spriteRenderable.destroy();
    }
}
