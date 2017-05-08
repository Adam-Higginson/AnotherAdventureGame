package com.adam.adventure.render.shader;

import static org.lwjgl.opengl.GL20.glUniform4f;

public class Uniform4f extends Uniform {

    public Uniform4f(final int uniformLocation) {
        super(uniformLocation);
    }

    public void useUniform(final float a, final float b, final float c, final float d) {
        glUniform4f(getUniformLocation(), a, b, c, d);
    }
}
