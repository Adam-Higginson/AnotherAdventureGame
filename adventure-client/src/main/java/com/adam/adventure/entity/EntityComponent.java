package com.adam.adventure.entity;

import com.adam.adventure.entity.component.TransformComponent;
import com.adam.adventure.entity.component.event.ComponentEvent;

import java.util.Optional;

public abstract class EntityComponent {

    private ComponentContainer componentContainer;


    public Entity getEntity() {
        return componentContainer.getEntity();
    }

    final void addToContainer(final ComponentContainer componentContainer) {
        this.componentContainer = componentContainer;
        this.componentContainer.addComponent(this);
    }

    public <T extends EntityComponent> Optional<T> getComponent(final Class<T> componentType) {
        return componentContainer.getComponent(componentType);
    }

    public TransformComponent getTransformComponent() {
        return componentContainer.getTransformComponent();
    }

    protected void activate() {
        // By default nothing happens
    }

    protected void update(final float deltaTime) {
        // By default nothing happens
    }

    protected void onComponentEvent(final ComponentEvent componentEvent) {
        //By default nothing happens
    }

    protected final void broadcastComponentEvent(final ComponentEvent componentEvent) {
        componentContainer.broadcastComponentEvent(componentEvent);
    }
}
