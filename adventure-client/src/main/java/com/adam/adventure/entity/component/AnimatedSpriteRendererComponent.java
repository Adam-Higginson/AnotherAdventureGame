package com.adam.adventure.entity.component;

import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.entity.component.event.MovementComponentEvent;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.render.renderable.SpriteRenderable;
import com.adam.adventure.render.sprite.Sprite;
import com.adam.adventure.render.sprite.SpriteAnimation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class AnimatedSpriteRendererComponent extends EntityComponent {
    private static final Logger LOG = LoggerFactory.getLogger(AnimatedSpriteRendererComponent.class);

    @Inject
    private RenderQueue rendererQueue;

    private final Sprite sprite;
    private final SpriteAnimation defaultSpriteAnimation;
    private final Map<MovementComponentEvent.MovementType, SpriteAnimation> eventToSpriteAnimation;
    private final Set<MovementComponentEvent.MovementType> stopAnimationEvents;
    private MovementComponentEvent.MovementType activeMovementType;
    private SpriteAnimation activeSpriteAnimation;

    public AnimatedSpriteRendererComponent(final Builder builder) {
        this(builder.sprite,
                builder.defaultSpriteAnimation,
                builder.eventToSpriteAnimation,
                builder.stopAnimationEvents);
    }

    public AnimatedSpriteRendererComponent(final Sprite sprite,
                                           final SpriteAnimation defaultSpriteAnimation,
                                           final Map<MovementComponentEvent.MovementType, SpriteAnimation> eventToSpriteAnimation,
                                           final Set<MovementComponentEvent.MovementType> stopAnimationEvents) {
        this.sprite = sprite;
        this.defaultSpriteAnimation = defaultSpriteAnimation;
        this.eventToSpriteAnimation = eventToSpriteAnimation;
        this.stopAnimationEvents = stopAnimationEvents;
    }


    @Override
    public void activate() {
        final SpriteRenderable spriteRenderable = new SpriteRenderable(getEntity(), this.sprite, 1);
        rendererQueue.addRenderable(spriteRenderable);
    }

    @Override
    protected void update(final float deltaTime) {
        if (activeSpriteAnimation != null) {
            activeSpriteAnimation.update(deltaTime, sprite);
        }
    }

    @Override
    public void onComponentEvent(final ComponentEvent componentEvent) {
        if (!(componentEvent instanceof MovementComponentEvent)) {
            return;
        }
        final MovementComponentEvent movementComponentEvent = (MovementComponentEvent) componentEvent;

        if (activeMovementType != movementComponentEvent.getMovementType()) {
            if (stopAnimationEvents.contains(movementComponentEvent.getMovementType()) && activeSpriteAnimation != null) {
                activeSpriteAnimation.setLooping(false);
            } else {
                LOG.debug("Current component event = {} new one = {}", activeMovementType, movementComponentEvent.getMovementType());
                final SpriteAnimation newSpriteAnimation = eventToSpriteAnimation
                        .getOrDefault(movementComponentEvent.getMovementType(), defaultSpriteAnimation);
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

        activeMovementType = movementComponentEvent.getMovementType();
    }

    public static class Builder {
        private final Sprite sprite;
        private SpriteAnimation defaultSpriteAnimation;
        private final Map<MovementComponentEvent.MovementType, SpriteAnimation> eventToSpriteAnimation;
        private final Set<MovementComponentEvent.MovementType> stopAnimationEvents;

        public Builder(final Sprite sprite) {
            this.sprite = sprite;
            this.eventToSpriteAnimation = new EnumMap<>(MovementComponentEvent.MovementType.class);
            this.stopAnimationEvents = EnumSet.noneOf(MovementComponentEvent.MovementType.class);
        }

        public Builder setDefaultSpriteAnimation(final SpriteAnimation defaultSpriteAnimation) {
            this.defaultSpriteAnimation = defaultSpriteAnimation;
            return this;
        }

        public Builder onEventSetAnimation(final MovementComponentEvent.MovementType event, final SpriteAnimation spriteAnimation) {
            eventToSpriteAnimation.put(event, spriteAnimation);
            return this;
        }

        public Builder onEventStopAnimation(final MovementComponentEvent.MovementType event) {
            this.stopAnimationEvents.add(event);
            return this;
        }

        public AnimatedSpriteRendererComponent build() {
            return new AnimatedSpriteRendererComponent(this);
        }
    }
}
