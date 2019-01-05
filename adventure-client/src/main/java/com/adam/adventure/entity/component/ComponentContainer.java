package com.adam.adventure.entity.component;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.component.event.ComponentEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class ComponentContainer {
    private final Entity entity;
    private final TransformComponent transformComponent;
    private final Map<Class<? extends EntityComponent>, EntityComponent> entityComponentTypeToInstance;

    public ComponentContainer(final Entity entity) {
        this.entity = entity;
        entityComponentTypeToInstance = new HashMap<>();
        transformComponent = new TransformComponent(this);
        addComponent(transformComponent);
    }

    public ComponentContainer addComponent(final EntityComponent component) {
        entityComponentTypeToInstance.put(component.getClass(), component);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends EntityComponent> Optional<T> getComponent(final Class<T> componentType) {
        final T entityComponent = (T) entityComponentTypeToInstance.get(componentType);
        return Optional.ofNullable(entityComponent);
    }

    public TransformComponent getTransformComponent() {
        return transformComponent;
    }

    public Collection<EntityComponent> getAllComponents() {
        return entityComponentTypeToInstance.values();
    }

    public void update(final float deltaTime) {
        getAllComponents().forEach(component -> component.update(deltaTime));
    }

    public Entity getEntity() {
        return entity;
    }

    void broadcastComponentEvent(final ComponentEvent componentEvent) {
        getAllComponents().forEach(component -> component.onComponentEvent(componentEvent));
    }

    public void activate() {
        getAllComponents().forEach(EntityComponent::activate);
    }
}
