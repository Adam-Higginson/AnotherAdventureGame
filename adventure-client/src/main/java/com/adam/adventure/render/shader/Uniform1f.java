package com.adam.adventure.render.shader;

import static org.lwjgl.opengl.GL20.glUniform1f;

public class Uniform1f extends Uniform {
    public Uniform1f(final int uniformLocation) {
        super(uniformLocation);
    }

    public void useUniform(final float value) {
        glUniform1f(getUniformLocation(), value);
    }

}
