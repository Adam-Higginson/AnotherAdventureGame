package com.adam.adventure.render;

import com.adam.adventure.entity.Entity;

public abstract class RenderableEntity<T extends Entity> implements Renderable {

    private final T entity;

    public RenderableEntity(final T entity) {
        this.entity = entity;
    }

    protected T getEntity() {
        return entity;
    }
}
