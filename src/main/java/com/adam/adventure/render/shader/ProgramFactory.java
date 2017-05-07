package com.adam.adventure.render.shader;

import com.adam.adventure.render.Renderer;
import org.lwjgl.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;

public class ProgramFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ProgramFactory.class);

    private final Renderer renderer;

    public ProgramFactory(final Renderer renderer) {
        this.renderer = renderer;
    }

    public Program registerProgramFromShaders(final Shader vertexShader, final Shader fragmentShader, final String programName) {
        final int programId = glCreateProgram();
        glAttachShader(programId, vertexShader.getShaderId());
        glAttachShader(programId, fragmentShader.getShaderId());
        glLinkProgram(programId);
        LOG.debug("Linking vertex shader id: {} and fragment shader id: {} using program id: {}",
                vertexShader.getShaderId(), fragmentShader.getShaderId(), programId);

        assertProgramLinkedSuccessfully(programId);

        glDeleteShader(vertexShader.getShaderId());
        glDeleteShader(fragmentShader.getShaderId());


        final Program program = new Program(programId, programName);
        renderer.registerProgram(program);
        return program;
    }

    private void assertProgramLinkedSuccessfully(final int programId) {
        final IntBuffer intbuf = BufferUtils.createIntBuffer(1);
        glGetProgramiv(programId, GL_LINK_STATUS, intbuf);
        final int success = intbuf.get(0);
        if (success == 0) {
            final String shaderInfoLog = glGetProgramInfoLog(programId);
            throw new ProgramException(shaderInfoLog);
        }
    }
}
