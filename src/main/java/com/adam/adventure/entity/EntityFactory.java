package com.adam.adventure.entity;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.update.event.NewLoopIterationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EntityFactory {
    private final List<Entity> entities;

    public EntityFactory(final EventBus eventBus) {
        this.entities = new ArrayList<>();
        eventBus.register(this);
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> T newEntity(final Supplier<T> entitySupplier) {
        final Entity entity = entitySupplier.get();
        entities.add(entity);
        return (T) entity;
    }

    @EventSubscribe
    @SuppressWarnings("unused")
    public void onUpdateEvent(final NewLoopIterationEvent newLoopIterationEvent) {
        entities.forEach(entity -> entity.update(newLoopIterationEvent.getElapsedTime()));
    }
}
