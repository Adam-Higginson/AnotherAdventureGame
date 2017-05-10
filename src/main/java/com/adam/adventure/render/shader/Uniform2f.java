package com.adam.adventure.render.shader;

import static org.lwjgl.opengl.GL20.glUniform2f;

public class Uniform2f extends Uniform {

    public Uniform2f(final int uniformLocation) {
        super(uniformLocation);
    }

    public void useUniform(final float a, final float b) {
        glUniform2f(getUniformLocation(), a, b);
    }
}
