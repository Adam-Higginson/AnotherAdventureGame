package com.adam.adventure.entity.component;

import com.adam.adventure.entity.EntityComponent;
import org.joml.Matrix4f;

public class TransformComponent extends EntityComponent {
    private Matrix4f transform;

    public TransformComponent() {
        transform = new Matrix4f();
    }

    public Matrix4f getTransform() {
        return transform;
    }

    public void setTransform(Matrix4f transform) {
        this.transform = transform;
    }
}
