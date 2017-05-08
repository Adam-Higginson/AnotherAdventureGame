package com.adam.adventure.render.camera.shader;

public abstract class Uniform {
    private final int uniformLocation;

    Uniform(final int uniformLocation) {
        this.uniformLocation = uniformLocation;
    }

    protected int getUniformLocation() {
        return uniformLocation;
    }
}
