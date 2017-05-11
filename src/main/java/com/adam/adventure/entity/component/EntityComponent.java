package com.adam.adventure.entity.component;

import com.adam.adventure.entity.Entity;

public interface EntityComponent {
    void update(final Entity target, float deltaTime);
}
