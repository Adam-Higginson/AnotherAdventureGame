package com.adam.adventure.render.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private static final Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);

    private Vector3f eye;
    private Vector3f target;

    public Camera(final Vector3f eye) {
        this.eye = eye;
        this.target = new Vector3f(0.0f, 0.0f, 0.0f);
    }


    public Vector3f getEye() {
        return eye;
    }

    public void setEye(final Vector3f eye) {
        this.eye = eye;
    }

    public Vector3f getTarget() {
        return target;
    }

    public void setTarget(final Vector3f target) {
        this.target = target;
    }


    public Matrix4f getLookAt() {
        return new Matrix4f().lookAt(eye, target, UP);
    }
}
