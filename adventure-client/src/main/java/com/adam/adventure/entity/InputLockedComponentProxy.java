package com.adam.adventure.entity;

import com.adam.adventure.entity.component.TransformComponent;
import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.event.LockInputEvent;

import java.util.Optional;

public class InputLockedComponentProxy extends EntityComponent {

    private final EventBus eventBus;
    private final EntityComponent entityComponent;
    private Object sourceObject;
    private boolean inputLocked;

    public InputLockedComponentProxy(final EventBus eventBus, final EntityComponent entityComponent) {
        this.eventBus = eventBus;
        this.entityComponent = entityComponent;
        this.eventBus.register(this);
    }

    @Override
    protected void update(final float deltaTime) {
        if (entityComponent.equals(sourceObject) || !inputLocked) {
            entityComponent.update(deltaTime);
        }
    }

    @EventSubscribe
    public void onEvent(final LockInputEvent event) {
        inputLocked = event.isLocked();
        sourceObject = event.getSource();
    }


    @Override
    public Entity getEntity() {
        return entityComponent.getEntity();
    }


    @Override
    public <T extends EntityComponent> Optional<T> getComponent(final Class<T> componentType) {
        return entityComponent.getComponent(componentType);
    }

    @Override
    public TransformComponent getTransformComponent() {
        return entityComponent.getTransformComponent();
    }

    @Override
    protected void activate() {
        entityComponent.activate();
    }

    @Override
    protected void destroy() {
        entityComponent.destroy();
    }

    @Override
    protected void onComponentEvent(final ComponentEvent componentEvent) {
        entityComponent.onComponentEvent(componentEvent);
    }
}
