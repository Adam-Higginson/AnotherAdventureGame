package com.adam.adventure.render.camera.shader;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class Program {
    private final int programId;
    private final String programName;
    private final Map<String, Uniform> uniformNameToUniform;

    public Program(final int programId, final String programName) {
        this.programId = programId;
        this.programName = programName;
        this.uniformNameToUniform = new HashMap<>();
    }

    public String getProgramName() {
        return programName;
    }

    public void useProgram() {
        glUseProgram(programId);
    }

    @SuppressWarnings("unchecked")
    public <T extends Uniform> T getUniform(final String uniformName, final Class<T> returnType) {
        //Lazy load
        if (uniformNameToUniform.containsKey(uniformName)) {
            return (T) uniformNameToUniform.get(uniformName);
        }

        final int uniformLocation = glGetUniformLocation(this.programId, uniformName);
        if (uniformLocation == -1) {
            throw new IllegalArgumentException("Uniform location for name: " + uniformName + " could not be found!");
        }

        try {
            final Uniform uniform = returnType.getConstructor(Integer.TYPE).newInstance(uniformLocation);
            uniformNameToUniform.put(uniformName, uniform);
            return (T) uniform;
        } catch (final NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

}
