package com.adam.adventure.render.camera.shader;

import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;

public class ShaderCompiler {

    public Shader compileVertexShader(final String shaderSource) {
        return compileShader(shaderSource, GL_VERTEX_SHADER);
    }

    public Shader compileFragmentShader(final String shaderSource) {
        return compileShader(shaderSource, GL_FRAGMENT_SHADER);
    }


    private Shader compileShader(final String shaderSource, final int glShaderType) {
        final int shaderId = glCreateShader(glShaderType);
        glShaderSource(shaderId, shaderSource);
        glCompileShader(shaderId);

        assertShaderCompiledSuccessfully(shaderId);

        return new Shader(shaderId);
    }

    private void assertShaderCompiledSuccessfully(final int shaderId) {
        final IntBuffer intbuf = BufferUtils.createIntBuffer(1);
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, intbuf);
        final int compiled = intbuf.get(0);
        if (compiled == 0) {
            final String shaderInfoLog = glGetShaderInfoLog(shaderId);
            throw new ShaderCompileException(shaderInfoLog);
        }
    }
}
