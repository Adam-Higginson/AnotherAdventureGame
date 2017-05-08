package com.adam.adventure.render.camera.shader;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

public class UniformMatrix4f extends Uniform {

    public UniformMatrix4f(final int uniformLocation) {
        super(uniformLocation);
    }

    public void useUniform(final Matrix4f matrix) {
        final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
        matrix.get(floatBuffer);
        glUniformMatrix4fv(getUniformLocation(), false, floatBuffer);
    }

}
