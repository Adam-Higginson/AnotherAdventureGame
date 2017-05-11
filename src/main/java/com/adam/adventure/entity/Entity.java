package com.adam.adventure.entity;

import com.adam.adventure.entity.component.EntityComponent;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Entity {
    private final Matrix4f transform;
    private final List<EntityComponent> components;

    Entity() {
        transform = new Matrix4f();
        components = new ArrayList<>();
    }

    public Matrix4f getTransform() {
        return transform;
    }

    public Entity addComponenet(final EntityComponent component) {
        components.add(component);
        return this;
    }

    public void update(final float deltaTime) {
        components.forEach(component -> component.update(this, deltaTime));
    }
}
