package com.adam.adventure.scene;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.event.EventBus;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class Scene {
    private static final Logger LOG = LoggerFactory.getLogger(Scene.class);

    private final String name;
    private final Set<Integer> entityIdsAlreadyInScene;
    private final Queue<Entity> entitiesToBeAdded;
    private final List<Entity> entities;
    private boolean active;

    public Scene(final EventBus eventBus, final String name) {
        this.name = name;
        this.entityIdsAlreadyInScene = new HashSet<>();
        this.entitiesToBeAdded = new LinkedList<>();
        this.entities = new ArrayList<>();
        eventBus.register(this);
    }

    public Scene addEntity(final Entity entity) {
        //Don't add if this entity is already in the scene!
        if (!entityIdsAlreadyInScene.contains(entity.getId())) {
            LOG.debug("Adding entity: {} to scene: {}", entity, name);
            entityIdsAlreadyInScene.add(entity.getId());

            // If we add an entity to the scene whilst iterating through existing entities we get a concurrent
            // modification exception. We queue the entities to be added and drain on next update call.
            if (active) {
                entitiesToBeAdded.add(entity);
            } else {
                //We can simply add the entity if we're not yet active as we won't be iterated over.
                entities.add(entity);
            }
        } else {
            LOG.debug("Not adding entity: {} to scene: {} as it is already present!", entity, name);
        }

        return this;
    }

    public List<Entity> getEntities() {
        return ImmutableList.copyOf(entities);
    }

    void activate() {
        active = true;
        LOG.info("Activating scene: {}", name);
        entities.forEach(Entity::activate);
        entities.forEach(Entity::afterActivate);
    }


    public void update(final float elapsedTime) {
        addNewEntitiesToScene();
        entities.forEach(entity -> entity.beforeUpdate(elapsedTime));
        entities.forEach(entity -> entity.update(elapsedTime));
        entities.forEach(entity -> entity.afterUpdate(elapsedTime));
    }


    public void destroy() {
        LOG.info("Destroying scene: {}", name);
        entities.forEach(Entity::destroy);
        active = false;
    }

    public String getName() {
        return name;
    }


    /**
     * Returns the first entity found which has the given component
     *
     * @param componentClass The component class which should be found.
     * @return The found entity, or empty if no such entity could be found.
     */
    public Optional<Entity> getEntityWithComponent(final Class<? extends EntityComponent> componentClass) {
        return entities.stream()
                .filter(entity -> entity.hasComponent(componentClass))
                .findFirst();
    }


    public List<Entity> findEntitiesByName(final String entityName) {
        return entities.stream()
                .filter(entity -> entity.getName().equals(entityName))
                .collect(Collectors.toList());
    }

    public <T extends EntityComponent> List<T> findEntityComponents(final Class<T> componentClass) {
        return entities.stream()
                .map(entity -> entity.getComponent(componentClass))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private void addNewEntitiesToScene() {
        //Drain the queue and add to scene.
        Entity entityToBeAdded = entitiesToBeAdded.poll();
        while (entityToBeAdded != null) {
            entities.add(entityToBeAdded);
            entityToBeAdded.activate();
            entityToBeAdded = entitiesToBeAdded.poll();
        }
    }

    public void removeEntities(final List<Entity> entitiesToRemove) {
        final Set<Integer> idsToRemove = entitiesToRemove.stream()
                .map(Entity::getId)
                .collect(Collectors.toSet());

        entities.removeIf(entity -> idsToRemove.contains(entity.getId()));
    }
}
