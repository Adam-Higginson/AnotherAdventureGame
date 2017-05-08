package com.adam.adventure.render.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private static final Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);

    private final float speed;
    private final Vector3f eye;
    private final Vector3f target;

    public Camera(final float speed, final Vector3f eye) {
        this.speed = speed;
        this.eye = eye;
        this.target = new Vector3f(0.0f, 0.0f, 0.0f);
    }


    public Camera moveUp(final float deltaTime) {
        final float amountToMove = speed * deltaTime;
        final Vector3f delta = new Vector3f(0.0f, amountToMove, 0.0f);
        eye.add(delta);
        target.add(delta);

        return this;
    }

    public Camera moveDown(final float deltaTime) {
        final float amountToMove = speed * deltaTime;
        final Vector3f delta = new Vector3f(0.0f, -amountToMove, 0.0f);
        eye.add(delta);
        target.add(delta);

        return this;
    }

    public Camera moveRight(final float deltaTime) {
        final float amountToMove = speed * deltaTime;
        final Vector3f delta = new Vector3f(amountToMove, 0.0f, 0.0f);
        eye.add(delta);
        target.add(delta);

        return this;
    }

    public Camera moveLeft(final float deltaTime) {
        final float amountToMove = speed * deltaTime;
        final Vector3f delta = new Vector3f(-amountToMove, 0.0f, 0.0f);
        eye.add(delta);
        target.add(delta);

        return this;
    }


    public Matrix4f getLookAt() {
        return new Matrix4f().lookAt(eye, target, UP);
    }
}
