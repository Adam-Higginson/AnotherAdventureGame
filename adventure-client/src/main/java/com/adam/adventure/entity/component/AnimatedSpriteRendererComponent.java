package com.adam.adventure.entity.component;

import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.render.renderable.SpriteRenderable;
import com.adam.adventure.render.sprite.Sprite;
import com.adam.adventure.render.texture.SpriteAnimation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class AnimatedSpriteRendererComponent extends EntityComponent {
    private static final Logger LOG = LoggerFactory.getLogger(AnimatedSpriteRendererComponent.class);

    private final Sprite sprite;
    private final Map<ComponentEvent, SpriteAnimation> eventToSpriteAnimation;
    private final Set<ComponentEvent> stopAnimationEvents;
    private ComponentEvent activeComponentEvent;
    private SpriteAnimation activeSpriteAnimation;

    public AnimatedSpriteRendererComponent(final ComponentContainer componentContainer,
                                           final Sprite sprite,
                                           final RenderQueue rendererQueue,
                                           final Map<ComponentEvent, SpriteAnimation> eventToSpriteAnimation,
                                           final Set<ComponentEvent> stopAnimationEvents) {
        super(componentContainer);
        this.sprite = sprite;
        this.eventToSpriteAnimation = eventToSpriteAnimation;
        this.stopAnimationEvents = stopAnimationEvents;
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
}
