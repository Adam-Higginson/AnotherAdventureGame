package com.adam.adventure.entity;

import org.joml.Matrix4f;

public class Entity {
    private final String name;
    private final ComponentContainer componentContainer;

    public Entity(final String name) {
        this.name = name;
        this.componentContainer = new ComponentContainer(this);
    }

    public Matrix4f getTransform() {
        return componentContainer.getTransformComponent().getTransform();
    }


    public Entity addComponent(final EntityComponent entityComponent) {
        entityComponent.addToContainer(componentContainer);
        return this;
    }

    public String getName() {
        return name;
    }

    /**
     * Tells the entity that it should now be active in the context of the current scene
     */
    public void activate() {
        componentContainer.activate();
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
