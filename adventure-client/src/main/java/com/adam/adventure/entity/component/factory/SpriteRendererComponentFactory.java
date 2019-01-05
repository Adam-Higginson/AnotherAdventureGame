package com.adam.adventure.entity.component.factory;

import com.adam.adventure.entity.component.ComponentContainer;
import com.adam.adventure.entity.component.EntityComponent;
import com.adam.adventure.entity.component.SpriteRendererComponent;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.render.sprite.Sprite;

public class SpriteRendererComponentFactory implements EntityComponentFactory {
    private final Sprite sprite;
    private final RenderQueue rendererQueue;

    public SpriteRendererComponentFactory(final Sprite sprite, final RenderQueue rendererQueue) {
        this.sprite = sprite;
        this.rendererQueue = rendererQueue;
    }


    @Override
    public void registerNewInstanceWithContainer(final ComponentContainer componentContainer) {
        final EntityComponent spriteRendererComponent = new SpriteRendererComponent(componentContainer, sprite, rendererQueue);
        componentContainer.addComponent(spriteRendererComponent);
    }
}
