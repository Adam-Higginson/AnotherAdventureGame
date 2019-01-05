package com.adam.adventure.render.shader;

public abstract class Uniform {
    private final int uniformLocation;

    Uniform(final int uniformLocation) {
        this.uniformLocation = uniformLocation;
    }

    protected int getUniformLocation() {
        return uniformLocation;
    }
}
