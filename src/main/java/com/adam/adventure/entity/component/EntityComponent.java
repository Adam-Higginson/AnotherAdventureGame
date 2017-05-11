package com.adam.adventure.entity.component;

import com.adam.adventure.entity.Entity;

public interface EntityComponent<T extends Entity> {
    void update(final T target, float deltaTime);
}
