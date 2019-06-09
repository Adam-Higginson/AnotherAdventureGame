package com.adam.adventure.entity.repository;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import com.adam.adventure.entity.component.AnimatedSpriteRendererComponent;
import com.adam.adventure.entity.component.event.MovementComponentEvent;
import com.adam.adventure.render.sprite.Sprite;
import com.adam.adventure.render.sprite.SpriteAnimation;
import com.adam.adventure.render.texture.Texture;
import com.adam.adventure.render.texture.TextureCache;
import com.adam.adventure.render.util.Rectangle;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class ClientEntityRepository implements EntityRepository {
    private static final int SKELETON_FRAMES_PER_ANIMATION = 9;
    private static final int SKELETON_SPRITE_SIZE = 64;

    private final TextureCache textureCache;
    private final EntityFactory entityFactory;

    @Inject
    public ClientEntityRepository(final TextureCache textureCache,
                                  final EntityFactory entityFactory) {
        this.textureCache = textureCache;
        this.entityFactory = entityFactory;
    }


    @Override
    public Entity buildNpcSkeletonEntity() {
        final Texture skeletonTexture = textureCache.getTexture("/assets/sprites/enemies/skeleton.png")
                .orElseThrow(() -> new IllegalStateException("Could not fetch skeleton texture from cache!"));



        final SpriteAnimation moveUpAnimation = buildSkeletonAnimation(12);
        final SpriteAnimation moveEastAnimation = buildSkeletonAnimation(9);
        final SpriteAnimation moveSouthAnimation = buildSkeletonAnimation(10);
        final SpriteAnimation moveWestAnimation = buildSkeletonAnimation(11);
        final Sprite sprite = new Sprite(skeletonTexture, new Rectangle(0.0f, 0.0f, 64f, 64f), 90f, 90f);

        return entityFactory.create("skeleton")
                .addComponent(new AnimatedSpriteRendererComponent.Builder(sprite)
                        .setDefaultSpriteAnimation(moveUpAnimation)
                        .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_NORTH, moveUpAnimation)
                        .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_EAST, moveEastAnimation)
                        .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_SOUTH, moveSouthAnimation)
                        .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_WEST, moveWestAnimation)
                        .onEventStopAnimation(MovementComponentEvent.MovementType.ENTITY_NO_MOVEMENT)
                        .build());
    }

    private SpriteAnimation buildSkeletonAnimation(final int startingRowIndex) {
        final SpriteAnimation.Builder builder = new SpriteAnimation.Builder(100, true);
        final float yOffset = startingRowIndex * SKELETON_SPRITE_SIZE;
        for (int i = 0; i < SKELETON_FRAMES_PER_ANIMATION; i++) {
            final float xOffset = (i * SKELETON_SPRITE_SIZE);
            final Rectangle frame = new Rectangle(xOffset, yOffset, SKELETON_SPRITE_SIZE, SKELETON_SPRITE_SIZE);
            builder.addAnimationFrame(frame);
            LOG.info("Adding rectangle: {}", frame);
        }

        return builder.build();
    }
}
