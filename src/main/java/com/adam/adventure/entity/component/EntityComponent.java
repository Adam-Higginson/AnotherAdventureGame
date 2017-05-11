package com.adam.adventure.entity.component;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.component.event.ComponentEvent;

public interface EntityComponent<T extends Entity> {
    void update(final T target, float deltaTime, ComponentContainer componentContainer);

    void onComponentEvent(ComponentEvent componentEvent);
}
