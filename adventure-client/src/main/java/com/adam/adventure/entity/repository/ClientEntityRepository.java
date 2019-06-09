package com.adam.adventure.entity.repository;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import com.adam.adventure.entity.component.AnimatedSpriteRendererComponent;
import com.adam.adventure.render.sprite.Sprite;
import com.adam.adventure.render.sprite.SpriteAnimation;
import com.adam.adventure.render.texture.Texture;
import com.adam.adventure.render.texture.TextureCache;
import com.adam.adventure.render.util.Rectangle;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class ClientEntityRepository implements EntityRepository {

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

        final SpriteAnimation moveUpAnimation = new SpriteAnimation.Builder(100, true)
                .addAnimationFrame(new Rectangle(0.0f, 0.0f, 64f, 64f))
                .build();
        final Sprite sprite = new Sprite(skeletonTexture, new Rectangle(0.0f, 0.0f, 64f, 64f), 90f, 90f);

        return entityFactory.create("skeleton")
                .addComponent(new AnimatedSpriteRendererComponent.Builder(sprite)
                        .setDefaultSpriteAnimation(moveUpAnimation)
                        .build());
    }
}
