package com.adam.adventure.entity.component;

import com.adam.adventure.entity.SpriteEntity;
import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.render.texture.SpriteAnimation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class AnimatedSpriteComponent implements EntityComponent<SpriteEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(AnimatedSpriteComponent.class);

    private final Map<ComponentEvent, SpriteAnimation> eventToSpriteAnimation;
    private final Set<ComponentEvent> stopAnimationEvents;
    private ComponentEvent activeComponentEvent;
    private SpriteAnimation activeSpriteAnimation;

    private AnimatedSpriteComponent(final Builder builder) {
        this.eventToSpriteAnimation = builder.eventToSpriteAnimation;
        this.stopAnimationEvents = builder.stopAnimationEvents;
    }

    @Override
    public void update(final SpriteEntity target, final float deltaTime, final ComponentContainer componentContainer) {
        if (activeSpriteAnimation != null) {
            activeSpriteAnimation.update(deltaTime, target.getSprite());
        }
    }

    @Override
    public void onComponentEvent(final ComponentEvent componentEvent) {
        if (activeComponentEvent != componentEvent) {
            if (stopAnimationEvents.contains(componentEvent) && activeSpriteAnimation != null) {
                activeSpriteAnimation.setLooping(false);
            } else {
                LOG.debug("Current component event = {} new one = {}", activeComponentEvent, componentEvent);
                final SpriteAnimation newSpriteAnimation = eventToSpriteAnimation.get(componentEvent);
                if (newSpriteAnimation != null) {
                    if (activeSpriteAnimation != null) {
                        activeSpriteAnimation.setPaused(true);
                        activeSpriteAnimation.reset();
                    }

                    activeSpriteAnimation = newSpriteAnimation;
                    activeSpriteAnimation.setPaused(false);
                    activeSpriteAnimation.setLooping(true);
                }
            }
        }

        activeComponentEvent = componentEvent;
    }


    public static class Builder {
        private final Map<ComponentEvent, SpriteAnimation> eventToSpriteAnimation;
        private final Set<ComponentEvent> stopAnimationEvents;

        public Builder() {
            eventToSpriteAnimation = new EnumMap<>(ComponentEvent.class);
            stopAnimationEvents = EnumSet.noneOf(ComponentEvent.class);
        }

        public Builder onEventSetAnimation(final ComponentEvent event, final SpriteAnimation spriteAnimation) {
            eventToSpriteAnimation.put(event, spriteAnimation);
            return this;
        }

        public Builder onEventStopAnimation(final ComponentEvent event) {
            this.stopAnimationEvents.add(event);
            return this;
        }

        public AnimatedSpriteComponent build() {
            return new AnimatedSpriteComponent(this);
        }
    }
}
