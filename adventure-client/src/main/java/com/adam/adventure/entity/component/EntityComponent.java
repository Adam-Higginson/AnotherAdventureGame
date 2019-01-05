package com.adam.adventure.entity.component;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.component.event.ComponentEvent;

import java.util.Optional;

public abstract class EntityComponent {

    private final ComponentContainer componentContainer;

    public EntityComponent(final ComponentContainer componentContainer) {
        this.componentContainer = componentContainer;
    }

    public Entity getEntity() {
        return componentContainer.getEntity();
    }

    public <T extends EntityComponent> Optional<T> getComponent(final Class<T> componentType) {
        return componentContainer.getComponent(componentType);
    }

    public TransformComponent getTransformComponent() {
        return componentContainer.getTransformComponent();
    }

    protected void activate() {
        //By default nothing happens
    }

    protected abstract void update(float deltaTime);

    protected abstract void onComponentEvent(ComponentEvent componentEvent);

    protected final void broadcastComponentEvent(final ComponentEvent componentEvent) {
        componentContainer.broadcastComponentEvent(componentEvent);
    }
}
