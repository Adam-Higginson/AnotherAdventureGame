package com.adam.adventure.render.shader;

import org.lwjgl.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;

public class ProgramFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ProgramFactory.class);

    public Program createProgramFromShaders(final Shader vertexShader, final Shader fragmentShader) {
        final int programId = glCreateProgram();
        glAttachShader(programId, vertexShader.getShaderId());
        glAttachShader(programId, fragmentShader.getShaderId());
        glLinkProgram(programId);
        LOG.debug("Linking vertex shader id: {} and fragment shader id: {} using program id: {}",
                vertexShader.getShaderId(), fragmentShader.getShaderId(), programId);

        assertProgramLinkedSuccessfully(programId);

        glDeleteShader(vertexShader.getShaderId());
        glDeleteShader(fragmentShader.getShaderId());

        return new Program(programId);
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
