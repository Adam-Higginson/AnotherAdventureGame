package com.adam.adventure.render.shader;

import static org.lwjgl.opengl.GL20.glUseProgram;

public class Program {
    private final int programId;

    public Program(final int programId) {
        this.programId = programId;
    }

    public void useProgram() {
        glUseProgram(programId);
    }
}
