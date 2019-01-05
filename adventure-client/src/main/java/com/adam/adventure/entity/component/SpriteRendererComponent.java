package com.adam.adventure.entity.component;

import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.render.renderable.SpriteRenderable;
import com.adam.adventure.render.sprite.Sprite;

public class SpriteRendererComponent extends EntityComponent {

    private final RenderQueue renderQueue;
    private final SpriteRenderable spriteRenderable;

    public SpriteRendererComponent(final ComponentContainer componentContainer, final Sprite sprite, final RenderQueue rendererQueue) {
        super(componentContainer);
        this.renderQueue = rendererQueue;
        this.spriteRenderable = new SpriteRenderable(getEntity(), sprite, 0);
        final SpriteRenderable spriteRenderable = this.spriteRenderable;
        rendererQueue.addRenderable(spriteRenderable);
    }

    @Override
    public void activate() {
        renderQueue.addRenderable(spriteRenderable);
    }

    @Override
    protected void update(final float deltaTime) {

    }

    @Override
    protected void onComponentEvent(final ComponentEvent componentEvent) {

    }
}
