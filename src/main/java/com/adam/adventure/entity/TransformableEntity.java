package com.adam.adventure.entity;

import org.joml.Matrix4f;

public abstract class TransformableEntity extends Entity {
    private Matrix4f transform;

    public TransformableEntity() {
        transform = new Matrix4f();
    }

    public Matrix4f getTransform() {
        return transform;
    }

    public Entity scale(final float x, final float y, final float z) {
        transform = transform.scale(x, y, z);
        return this;
    }

}
