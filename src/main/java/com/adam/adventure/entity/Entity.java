package com.adam.adventure.entity;

import com.adam.adventure.entity.component.ComponentContainer;
import com.adam.adventure.entity.component.factory.EntityComponentFactory;
import org.joml.Matrix4f;

public class Entity {
    private final String name;
    private final ComponentContainer componentContainer;

    public Entity(final String name) {
        this.name = name;
        componentContainer = new ComponentContainer(this);
    }

    public Matrix4f getTransform() {
        return componentContainer.getTransformComponent().getTransform();
    }

    public Entity addComponent(final EntityComponentFactory componentFactory) {
        componentFactory.registerNewInstanceWithContainer(componentContainer);
        return this;
    }

    public String getName() {
        return name;
    }

    public void update(final float deltaTime) {
        componentContainer.update(deltaTime);
    }

    @Override
    public String toString() {
        return "Entity{" +
                "name='" + name + '\'' +
                '}';
    }
}
