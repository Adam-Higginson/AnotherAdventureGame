package com.adam.adventure.render.texture;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.scene.NewSceneEvent;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class TextureCache {
    private final TextureFactory textureFactory;
    private final Map<String, Texture> textureNameToTexture;

    @Inject
    public TextureCache(final TextureFactory textureFactory, final EventBus eventBus) {
        this.textureFactory = textureFactory;
        textureNameToTexture = new HashMap<>();
        eventBus.register(this);
    }


    public Optional<Texture> getTexture(final String textureName) {
        return Optional.ofNullable(textureNameToTexture.get(textureName));
    }

    @EventSubscribe
    public void onNewScene(final NewSceneEvent newSceneEvent) throws IOException {
        LOG.info("Caching textures for scene: {}", newSceneEvent.getSceneName());

        //For now just cache the same textures
        textureNameToTexture.clear();

        cacheTexture("/assets/sprites/enemies/skeleton.png");
    }

    private void cacheTexture(final String textureName) throws IOException {
        textureNameToTexture.put(textureName,
                textureFactory.loadTextureFromFileNameInResources(textureName));
        LOG.info("Successfully cached texture: {}", textureName);
    }
}
