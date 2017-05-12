package com.adam.adventure.entity;

import com.adam.adventure.entity.component.ComponentContainer;
import com.adam.adventure.entity.component.EntityComponent;
import org.joml.Matrix4f;

public class Entity<T extends Entity<T>> {
    private final Matrix4f transform;
    private final ComponentContainer componentContainer;

    public Entity() {
        transform = new Matrix4f();
        componentContainer = new ComponentContainer();
    }

    public Matrix4f getTransform() {
        return transform;
    }

    @SuppressWarnings("unchecked")
    public T addComponent(final EntityComponent component) {
        componentContainer.addComponent(component);
        return (T) this;
    }

    public void update(final float deltaTime) {
        componentContainer.update(this, deltaTime);
    }
}
