package com.adam.adventure.entity.component;

import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.render.renderable.SpriteRenderable;
import com.adam.adventure.render.sprite.Sprite;

public class SpriteRendererComponent extends EntityComponent {

    private final Sprite sprite;
    private final RenderQueue renderQueue;

    public SpriteRendererComponent(final ComponentContainer componentContainer,
                                   final Sprite sprite,
                                   final RenderQueue rendererQueue) {
        super(componentContainer);
        this.sprite = sprite;
        this.renderQueue = rendererQueue;
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
