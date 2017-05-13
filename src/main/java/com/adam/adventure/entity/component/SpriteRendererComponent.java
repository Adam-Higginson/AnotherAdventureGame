package com.adam.adventure.entity.component;

import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.render.renderable.SpriteRenderable;
import com.adam.adventure.render.sprite.Sprite;

public class SpriteRendererComponent extends EntityComponent {


    public SpriteRendererComponent(final ComponentContainer componentContainer, final Sprite sprite, final RenderQueue rendererQueue) {
        super(componentContainer);
        final SpriteRenderable spriteRenderable = new SpriteRenderable(getEntity(), sprite, 0);
        rendererQueue.addRenderable(spriteRenderable);
    }

    @Override
    protected void update(final float deltaTime) {

    }

    @Override
    protected void onComponentEvent(final ComponentEvent componentEvent) {

    }
}
