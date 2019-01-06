package com.adam.adventure.scene;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.render.Renderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private static final Logger LOG = LoggerFactory.getLogger(Scene.class);

    private final String name;
    private final List<Entity> entities;
    private final Renderer renderer;

    Scene(final EventBus eventBus, final String name, final Renderer renderer) {
        this.name = name;
        this.renderer = renderer;
        entities = new ArrayList<>();
        eventBus.register(this);
    }

    public Scene addEntity(final Entity entity) {
        entities.add(entity);
        return this;
    }

    void activate() {
        LOG.info("Activating scene: {}", name);
        entities.forEach(Entity::activate);
        renderer.initialise();
    }

    void destroy() {
        LOG.info("Destroying scene: {}", name);
        entities.forEach(Entity::destroy);
    }

    public String getName() {
        return name;
    }

    public void update(final float elapsedTime) {
        entities.forEach(entity -> entity.update(elapsedTime));
    }


}
