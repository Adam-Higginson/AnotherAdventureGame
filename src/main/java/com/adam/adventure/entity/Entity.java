package com.adam.adventure.entity;

import com.adam.adventure.entity.component.ComponentContainer;
import com.adam.adventure.entity.component.EntityComponent;
import org.joml.Matrix4f;

public class Entity {
    private final Matrix4f transform;
    private final ComponentContainer componentContainer;

    public Entity() {
        transform = new Matrix4f();
        componentContainer = new ComponentContainer();
    }

    public Matrix4f getTransform() {
        return transform;
    }

    public Entity addComponent(final EntityComponent component) {
        componentContainer.addComponent(component);
        return this;
    }

    public void update(final float deltaTime) {
        componentContainer.update(this, deltaTime);
    }
}
