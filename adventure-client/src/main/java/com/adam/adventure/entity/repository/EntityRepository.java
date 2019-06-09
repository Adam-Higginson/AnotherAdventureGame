package com.adam.adventure.entity.repository;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import com.adam.adventure.entity.component.AnimatedSpriteRendererComponent;
import com.adam.adventure.entity.component.event.MovementComponentEvent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.WriteUiConsoleErrorEvent;
import com.adam.adventure.render.sprite.Sprite;
import com.adam.adventure.render.texture.SpriteAnimation;
import com.adam.adventure.render.texture.Texture;
import com.adam.adventure.render.texture.TextureFactory;
import com.adam.adventure.render.util.Rectangle;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class EntityRepository {

    private final TextureFactory textureFactory;
    private final EntityFactory entityFactory;
    private final EventBus eventBus;

    @Inject
    public EntityRepository(final TextureFactory textureFactory,
                            final EntityFactory entityFactory,
                            final EventBus eventBus) {
        this.textureFactory = textureFactory;
        this.entityFactory = entityFactory;
        this.eventBus = eventBus;
    }

    public Entity buildEntityFromName(final String entityName) {
        try {
            //Obviously this could get a lot more complicated
            if ("npc_skeleton".equals(entityName)) {
                return createSkeletonEntity();
            } else {
                LOG.warn("Not sure what to do with entity: {}, creating default entity", entityName);
                return entityFactory.create(entityName);
            }
        } catch (final IOException e) {
            LOG.error("Exception when trying to create entity! Returning empty entity", e);
            eventBus.publishEvent(new WriteUiConsoleErrorEvent("Could not create entity! Message was: " + e.getMessage()));
            return entityFactory.create(entityName);
        }
    }


    private Entity createSkeletonEntity() throws IOException {
        //All this loading should be done before!
        final Texture skeletonTexture;
        try (final InputStream skeletonTextureInputStream = this.getClass().getResourceAsStream("/assets/sprites/enemies/skeleton.png")) {
            skeletonTexture = textureFactory.loadTextureFromPng(skeletonTextureInputStream);
        }


        final SpriteAnimation moveUpAnimation = new SpriteAnimation.Builder(100, true)
                .addAnimationFrame(new Rectangle(0.0f, 0.0f, 64f, 64f))
                .build();


        final Sprite sprite = new Sprite(skeletonTexture, new Rectangle(0.0f, 0.0f, 64f, 64f), 90f, 90f);

        return entityFactory.create("npc_skeleton")
                .addComponent(new AnimatedSpriteRendererComponent.Builder(sprite)
                        .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_NO_MOVEMENT, moveUpAnimation)
                        .build());
    }
}
