package com.adam.adventure.scene;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Scene {
    private static final Logger LOG = LoggerFactory.getLogger(Scene.class);

    private final String name;
    private final Queue<Entity> entitiesToBeAdded;
    private final List<Entity> entities;
    private boolean active;

    public Scene(final EventBus eventBus, final String name) {
        this.name = name;
        this.entitiesToBeAdded = new LinkedList<>();
        this.entities = new ArrayList<>();
        eventBus.register(this);
    }

    public Scene addEntity(final Entity entity) {
        // If we add an entity to the scene whilst iterating through existing entities we get a concurrent
        // modification exception. We queue the entities to be added and drain on next update call.
        if (active) {
            entitiesToBeAdded.add(entity);
        } else {
            //We can simply add the entity if we're not yet active as we won't be iterated over.
            entities.add(entity);
        }

        return this;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    void activate() {
        active = true;
        LOG.info("Activating scene: {}", name);
        entities.forEach(Entity::activate);
    }

    void destroy() {
        LOG.info("Destroying scene: {}", name);
        entities.forEach(Entity::destroy);
        active = false;
    }

    public String getName() {
        return name;
    }

    public void update(final float elapsedTime) {
        addNewEntitiesToScene();
        entities.forEach(entity -> entity.update(elapsedTime));
    }

    private void addNewEntitiesToScene() {
        //Drain the queue and add to scene.
        if (!entitiesToBeAdded.isEmpty()) {
            Entity entityToBeAdded = entitiesToBeAdded.poll();
            while (entityToBeAdded != null) {
                entities.add(entityToBeAdded);
                entityToBeAdded.activate();
                entityToBeAdded = entitiesToBeAdded.poll();
            }
        }
    }

    void forceDestroy() {
        entities.forEach(entity -> entity.setShouldDestroyOnSceneChange(true));
        destroy();
    }
}
