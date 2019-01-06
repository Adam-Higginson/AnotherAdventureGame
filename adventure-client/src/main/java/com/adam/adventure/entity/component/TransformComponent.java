package com.adam.adventure.entity.component;

import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.entity.component.event.ComponentEvent;
import org.joml.Matrix4f;

public class TransformComponent extends EntityComponent {
    private final Matrix4f transform;

    public TransformComponent() {
        transform = new Matrix4f();
    }

    @Override
    protected void update(final float deltaTime) {

    }

    @Override
    protected void onComponentEvent(final ComponentEvent componentEvent) {

    }

    public Matrix4f getTransform() {
        return transform;
    }
}
