package com.adam.adventure.entity;

import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import org.joml.Matrix4f;

import javax.inject.Inject;

public class Entity {
    private final String name;
    private final Injector injector;
    private final ComponentContainer componentContainer;
    private boolean shouldDestroyOnSceneChange;
    private boolean active;

    @Inject
    Entity(@Assisted final String name, final Injector injector) {
        this.name = name;
        this.injector = injector;
        this.componentContainer = new ComponentContainer(this);
    }

    public Matrix4f getTransform() {
        return componentContainer.getTransformComponent().getTransform();
    }

    public void setTransform(final Matrix4f transform) {
        componentContainer.getTransformComponent().setTransform(transform);
    }

    /**
     * Adds the component to this entity and injects all required fields
     *
     * @param entityComponent the component to add
     * @return this entity, for method chaining
     */
    public Entity addComponent(final EntityComponent entityComponent) {
        entityComponent.addToContainer(componentContainer);
        injector.injectMembers(entityComponent);

        return this;
    }

    public String getName() {
        return name;
    }

    /**
     * Tells the entity that it should now be active in the context of the current scene
     */
    public void activate() {
        if (!active) {
            active = true;
            componentContainer.activate();
        }
    }

    /**
     * Tells an entity it should be removed from the current scene.
     */
    public void destroy() {
        if (shouldDestroyOnSceneChange) {
            componentContainer.destroy();
            active = false;
        }
    }

    public void update(final float deltaTime) {
        if (active) {
            componentContainer.update(deltaTime);
        }
    }

    public Entity setShouldDestroyOnSceneChange(final boolean shouldDestroyOnSceneChange) {
        this.shouldDestroyOnSceneChange = shouldDestroyOnSceneChange;
        return this;
    }

    public boolean shouldDestroyOnSceneChange() {
        return shouldDestroyOnSceneChange;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "name='" + name + '\'' +
                '}';
    }
}
