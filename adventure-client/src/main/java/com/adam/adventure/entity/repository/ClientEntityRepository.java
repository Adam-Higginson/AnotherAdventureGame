package com.adam.adventure.entity.repository;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityConstructionException;
import com.adam.adventure.entity.EntityFactory;
import com.adam.adventure.entity.component.AnimatedSpriteRendererComponent;
import com.adam.adventure.entity.component.CameraTargetComponent;
import com.adam.adventure.entity.component.KeyboardMovementComponent;
import com.adam.adventure.entity.component.event.MovementComponentEvent;
import com.adam.adventure.entity.component.network.NetworkTransformComponent;
import com.adam.adventure.render.sprite.Sprite;
import com.adam.adventure.render.sprite.SpriteAnimation;
import com.adam.adventure.render.texture.Texture;
import com.adam.adventure.render.texture.TextureCache;
import com.adam.adventure.render.texture.TextureFactory;
import com.adam.adventure.render.util.Rectangle;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ClientEntityRepository implements EntityRepository {
    private static final int SKELETON_FRAMES_PER_ANIMATION = 9;
    private static final int SKELETON_SPRITE_SIZE = 64;

    private final TextureFactory textureFactory;
    private final TextureCache textureCache;
    private final EntityFactory entityFactory;

    @Inject
    public ClientEntityRepository(
            final TextureFactory textureFactory,
            final TextureCache textureCache,
            final EntityFactory entityFactory) {
        this.textureFactory = textureFactory;
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

    @Override
    public Entity buildPlayerEntity() {
        final Texture playerTexture;
        try {
            try (final InputStream playerTextureInputStream = this.getClass().getResourceAsStream("/assets/sprites/player/link.png")) {
                playerTexture = textureFactory.loadTextureFromPng(playerTextureInputStream);
            }
        } catch (final IOException e) {
            throw new EntityConstructionException(e);
        }

        final SpriteAnimation moveUpAnimation = new SpriteAnimation.Builder(100, true)
                .addAnimationFrame(new Rectangle(0.0f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(30f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(60f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(90f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(120f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(150f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(180f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(210f, 150.0f, 30f, 30f))
                .build();

        final SpriteAnimation moveEastAnimation = new SpriteAnimation.Builder(100, true)
                .addAnimationFrame(new Rectangle(240f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(270f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(300f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(330f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(360f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(390f, 150.0f, 30f, 30f))
                .build();

        final SpriteAnimation moveWestAnimation = new SpriteAnimation.Builder(100, true)
                .addAnimationFrame(new Rectangle(240f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(270f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(300f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(330f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(360f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(390f, 240.0f, 30f, 30f))
                .build();


        final SpriteAnimation moveDownAnimation = new SpriteAnimation.Builder(100, true)
                .addAnimationFrame(new Rectangle(0.0f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(30f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(60f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(90f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(120f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(150f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(180f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(210f, 240.0f, 30f, 30f))
                .build();

        final KeyboardMovementComponent keyboardMovementComponent = new KeyboardMovementComponent(.2f);
        final Sprite sprite = new Sprite(playerTexture, new Rectangle(0.0f, 0.0f, 30f, 30f), 90f, 90f);
        final AnimatedSpriteRendererComponent animatedSpriteRendererComponent = new AnimatedSpriteRendererComponent.Builder(sprite)
                .onEventStopAnimation(MovementComponentEvent.MovementType.ENTITY_NO_MOVEMENT)
                .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_NORTH, moveUpAnimation)
                .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_EAST, moveEastAnimation)
                .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_WEST, moveWestAnimation)
                .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_SOUTH, moveDownAnimation)
                .build();
        final CameraTargetComponent cameraTargetComponent = new CameraTargetComponent();


        //Create player

        return entityFactory.create(PLAYER_NAME)
                .addComponent(new NetworkTransformComponent(true))
                .addComponent(keyboardMovementComponent)
                .addComponent(animatedSpriteRendererComponent)
                .addComponent(cameraTargetComponent);
    }

    @Override
    public Entity buildOtherPlayerEntity() {
        final Texture playerTexture;
        try {
            try (final InputStream playerTextureInputStream = this.getClass().getResourceAsStream("/assets/sprites/player/link.png")) {
                playerTexture = textureFactory.loadTextureFromPng(playerTextureInputStream);
            }
        } catch (final IOException e) {
            throw new EntityConstructionException(e);
        }

        final SpriteAnimation moveUpAnimation = new SpriteAnimation.Builder(100, true)
                .addAnimationFrame(new Rectangle(0.0f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(30f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(60f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(90f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(120f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(150f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(180f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(210f, 150.0f, 30f, 30f))
                .build();

        final SpriteAnimation moveEastAnimation = new SpriteAnimation.Builder(100, true)
                .addAnimationFrame(new Rectangle(240f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(270f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(300f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(330f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(360f, 150.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(390f, 150.0f, 30f, 30f))
                .build();

        final SpriteAnimation moveWestAnimation = new SpriteAnimation.Builder(100, true)
                .addAnimationFrame(new Rectangle(240f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(270f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(300f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(330f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(360f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(390f, 240.0f, 30f, 30f))
                .build();


        final SpriteAnimation moveDownAnimation = new SpriteAnimation.Builder(100, true)
                .addAnimationFrame(new Rectangle(0.0f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(30f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(60f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(90f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(120f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(150f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(180f, 240.0f, 30f, 30f))
                .addAnimationFrame(new Rectangle(210f, 240.0f, 30f, 30f))
                .build();

        final Sprite sprite = new Sprite(playerTexture, new Rectangle(0.0f, 0.0f, 30f, 30f), 90f, 90f);

        return entityFactory.create(OTHER_PLAYER)
                .addComponent(new NetworkTransformComponent(false))
                .addComponent(new AnimatedSpriteRendererComponent.Builder(sprite)
                        .onEventStopAnimation(MovementComponentEvent.MovementType.ENTITY_NO_MOVEMENT)
                        .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_NORTH,
                                new SpriteAnimation.Builder(moveUpAnimation).build())
                        .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_EAST,
                                new SpriteAnimation.Builder(moveEastAnimation).build())
                        .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_WEST,
                                new SpriteAnimation.Builder(moveWestAnimation).build())
                        .onEventSetAnimation(MovementComponentEvent.MovementType.ENTITY_MOVE_SOUTH,
                                new SpriteAnimation.Builder(moveDownAnimation).build())
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
