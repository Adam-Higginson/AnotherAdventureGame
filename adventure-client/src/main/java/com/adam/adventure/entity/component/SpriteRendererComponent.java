package com.adam.adventure.entity.component;

import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.render.renderable.SpriteRenderable;
import com.adam.adventure.render.sprite.Sprite;

import javax.inject.Inject;

public class SpriteRendererComponent extends EntityComponent {

    @Inject
    private RenderQueue renderQueue;

    private final Sprite sprite;

    public SpriteRendererComponent(final Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void activate() {
        final SpriteRenderable spriteRenderable = new SpriteRenderable(getEntity(), sprite, 0);
        renderQueue.addRenderable(spriteRenderable);
    }

    @Override
    protected void update(final float deltaTime) {

    }

    @Override
    protected void onComponentEvent(final ComponentEvent componentEvent) {

    }
}
