package com.adam.adventure.render.shader;

import static org.lwjgl.opengl.GL20.glUniform1i;

public class Uniform1i extends Uniform {

    Uniform1i(final int uniformLocation) {
        super(uniformLocation);
    }

    public void useUniform(final int value) {
        glUniform1i(getUniformLocation(), value);
    }
}
