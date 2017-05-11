package com.adam.adventure.entity;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.update.event.UpdateEvent;

import java.util.ArrayList;
import java.util.List;

public class EntityFactory {
    private final List<Entity> entities;

    public EntityFactory(final EventBus eventBus) {
        this.entities = new ArrayList<>();
        eventBus.register(this);
    }

    public Entity newEntity() {
        final Entity entity = new Entity();
        entities.add(entity);
        return entity;
    }

    @EventSubscribe
    @SuppressWarnings("unused")
    public void onUpdateEvent(final UpdateEvent updateEvent) {
        entities.forEach(entity -> entity.update(updateEvent.getElapsedTime()));
    }
}
