package com.adam.adventure.entity.component;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.component.event.ComponentEvent;

import java.util.ArrayList;
import java.util.List;

public class ComponentContainer {
    private final List<EntityComponent<Entity>> components;

    public ComponentContainer() {
        components = new ArrayList<>();
    }

    public ComponentContainer addComponent(final EntityComponent component) {
        components.add(component);
        return this;
    }

    public void update(final Entity target, final float deltaTime) {
        components.forEach(component -> component.update(target, deltaTime, this));
    }

    public void broadcastComponentEvent(final ComponentEvent componentEvent) {
        components.forEach(component -> component.onComponentEvent(componentEvent));
    }
}
