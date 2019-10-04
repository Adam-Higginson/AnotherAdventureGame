package com.adam.adventure.entity;

import com.google.inject.Injector;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Entity {
    private static final Logger LOG = LoggerFactory.getLogger(Entity.class);

    private final String name;
    private final int id;
    private final Injector injector;
    private final ComponentContainer componentContainer;
    private boolean shouldDestroyOnSceneChange = true;
    private boolean active;

    protected Entity(final String name, final int id, final Injector injector) {
        this.name = name;
        this.id = id;
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

    /**
     * @return true if this entity contains the given component, false otherwise.
     */
    public boolean hasComponent(final Class<? extends EntityComponent> componentClass) {
        return componentContainer.getComponent(componentClass).isPresent();
    }

    public String getName() {
        return name;
    }

    public <T extends EntityComponent> Optional<T> getComponent(final Class<T> componentType) {
        return componentContainer.getComponent(componentType);
    }

    /**
     * Tells the entity that it should now be active in the context of the current scene
     */
    public void activate() {
        if (!active) {
            active = true;
            LOG.debug("Activating entity (name={}, id={})", name, id);
            componentContainer.activate();
            LOG.debug("Activated entity (name={}, id={})", name, id);
        }
    }


    /**
     * Called after the activate method has been called
     */
    public void afterActivate() {
        if (active) {
            componentContainer.afterActivate();
        }
    }

    /**
     * Tells an entity it should be removed from the current scene.
     */
    public void destroy() {
        if (shouldDestroyOnSceneChange) {
            LOG.debug("Destroying entity (name={}, id={})", name, id);
            componentContainer.destroy();
            active = false;
            LOG.debug("Destroyed entity (name={}, id={})", name, id);
        }
    }

    public void beforeUpdate(final float deltaTime) {
        if (active) {
            componentContainer.beforeUpdate(deltaTime);
        }
    }

    public void update(final float deltaTime) {
        if (active) {
            componentContainer.update(deltaTime);
        }
    }

    public void afterUpdate(final float deltaTime) {
        if (active) {
            componentContainer.afterUpdate(deltaTime);
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
                ", id=" + id +
                '}';
    }

    public int getId() {
        return id;
    }
}
