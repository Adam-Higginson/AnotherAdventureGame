package com.adam.adventure.entity.component.factory;

import com.adam.adventure.entity.component.AnimatedSpriteRendererComponent;
import com.adam.adventure.entity.component.ComponentContainer;
import com.adam.adventure.entity.component.EntityComponent;
import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.render.sprite.Sprite;
import com.adam.adventure.render.texture.SpriteAnimation;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class AnimatedSpriteRendererComponentFactory implements EntityComponentFactory {

    private final Sprite sprite;
    private final RenderQueue rendererQueue;
    private final Map<ComponentEvent, SpriteAnimation> eventToSpriteAnimation;
    private final Set<ComponentEvent> stopAnimationEvents;


    public AnimatedSpriteRendererComponentFactory(final Builder builder) {
        this.sprite = builder.sprite;
        this.rendererQueue = builder.renderQueue;
        this.eventToSpriteAnimation = builder.eventToSpriteAnimation;
        this.stopAnimationEvents = builder.stopAnimationEvents;
    }

    @Override
    public void registerNewInstanceWithContainer(final ComponentContainer componentContainer) {
        final EntityComponent animatedSpriteComponent = new AnimatedSpriteRendererComponent(componentContainer, sprite, rendererQueue, eventToSpriteAnimation, stopAnimationEvents);
        componentContainer.addComponent(animatedSpriteComponent);
    }


    public static class Builder {
        private final Sprite sprite;
        private final RenderQueue renderQueue;
        private final Map<ComponentEvent, SpriteAnimation> eventToSpriteAnimation;
        private final Set<ComponentEvent> stopAnimationEvents;

        public Builder(final Sprite sprite, final RenderQueue renderQueue) {
            this.sprite = sprite;
            this.renderQueue = renderQueue;
            this.eventToSpriteAnimation = new EnumMap<>(ComponentEvent.class);
            this.stopAnimationEvents = EnumSet.noneOf(ComponentEvent.class);
        }

        public Builder onEventSetAnimation(final ComponentEvent event, final SpriteAnimation spriteAnimation) {
            eventToSpriteAnimation.put(event, spriteAnimation);
            return this;
        }

        public Builder onEventStopAnimation(final ComponentEvent event) {
            this.stopAnimationEvents.add(event);
            return this;
        }

        public AnimatedSpriteRendererComponentFactory build() {
            return new AnimatedSpriteRendererComponentFactory(this);
        }
    }
}
