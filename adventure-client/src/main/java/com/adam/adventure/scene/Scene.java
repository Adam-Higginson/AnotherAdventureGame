package com.adam.adventure.scene;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.update.event.NewLoopIterationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private static final Logger LOG = LoggerFactory.getLogger(Scene.class);

    private final String sceneName;
    private final List<Entity> entities;
    private final Renderer renderer;

    public Scene(final EventBus eventBus, final String sceneName, final Renderer renderer) {
        this.sceneName = sceneName;
        this.renderer = renderer;
        entities = new ArrayList<>();
        eventBus.register(this);
    }

    public Scene addEntity(final Entity entity) {
        entities.add(entity);
        return this;
    }

    public void activateScene() {
        LOG.info("Activating scene: {}", sceneName);
        renderer.initialise();
    }

    @EventSubscribe
    @SuppressWarnings("unused")
    public void onUpdateEvent(final NewLoopIterationEvent newLoopIterationEvent) {
        entities.forEach(entity -> entity.update(newLoopIterationEvent.getElapsedTime()));
    }
}
