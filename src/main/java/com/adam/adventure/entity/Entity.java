package com.adam.adventure.entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public abstract class Entity {
    private Vector3f position;
    private Matrix4f translation;

    public Vector3f getPosition() {
        return position;
    }

    public Matrix4f getTranslation() {
        return translation;
    }
}
