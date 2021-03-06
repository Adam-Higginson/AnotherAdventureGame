package com.adam.adventure.module;

import com.adam.adventure.entity.EntityModule;
import com.adam.adventure.entity.component.tilemap.data.JsonTileMapLoader;
import com.adam.adventure.entity.component.tilemap.data.JsonTileSetLoader;
import com.adam.adventure.entity.component.tilemap.data.TileMapLoader;
import com.adam.adventure.entity.component.tilemap.data.TileSetLoader;
import com.adam.adventure.entity.repository.ClientEntityRepository;
import com.adam.adventure.entity.repository.EntityRepository;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.input.InputManager;
import com.adam.adventure.loop.LoopIteration;
import com.adam.adventure.loop.SleepingLoopIteration;
import com.adam.adventure.render.RenderQueue;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.render.camera.Camera;
import com.adam.adventure.render.texture.TextureCache;
import com.adam.adventure.render.texture.TextureFactory;
import com.adam.adventure.scene.SceneManager;
import com.adam.adventure.update.PublishEventUpdateStrategy;
import com.adam.adventure.update.UpdateStrategy;
import com.adam.adventure.window.Window;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;
import org.joml.Vector3f;

import javax.inject.Singleton;

public class AdventureClientModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EventBus.class).toInstance(new EventBus());
        bind(Camera.class).toInstance(new Camera(new Vector3f(0.0f, 0.0f, 1.0f)));
        bind(UpdateStrategy.class).to(PublishEventUpdateStrategy.class).in(Singleton.class);
        bind(LoopIteration.class).to(SleepingLoopIteration.class).in(Singleton.class);
        bind(InputManager.class).in(Singleton.class);
        bind(TextureFactory.class).in(Singleton.class);
        bind(Renderer.class).in(Singleton.class);
        bind(RenderQueue.class).in(Singleton.class);
        bind(SceneManager.class).in(Singleton.class);
        bind(TextureCache.class).in(Singleton.class);
        bind(EntityRepository.class).to(ClientEntityRepository.class).in(Singleton.class);
        bind(TileMapLoader.class).to(JsonTileMapLoader.class).in(Singleton.class);
        bind(TileSetLoader.class).to(JsonTileSetLoader.class).in(Singleton.class);

        install(new EntityModule());

        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Timed.class),
                new TimedInterceptor());
    }

    @Provides
    @Singleton
    public Window buildWindow(final EventBus eventBus) {
        return new Window.Builder(800, 600, eventBus)
                .withTitle("Yet another adventure game")
                .withIsVisible(true)
                .withIsResizable(true)
                .build();
    }
}
