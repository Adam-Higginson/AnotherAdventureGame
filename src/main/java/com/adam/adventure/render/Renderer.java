package com.adam.adventure.render;

import com.adam.adventure.render.camera.Camera;
import com.adam.adventure.render.shader.Program;
import com.adam.adventure.render.shader.UniformMatrix4f;
import com.adam.adventure.render.vertex.*;
import com.adam.adventure.window.Window;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private static final Logger LOG = LoggerFactory.getLogger(Renderer.class);

    private final VertexBufferFactory vertexBufferFactory;
    private final ElementArrayBufferFactory elementArrayBufferFactory;
    private final VertexArrayFactory vertexArrayFactory;
    private final Map<String, Program> programNametoProgram;
    private final RenderQueue renderQueue;
    private final Window window;
    private Camera camera;
    private Matrix4f viewMatrix;
    private Matrix4f projectionMatrix;

    public Renderer(final RenderQueue renderQueue, final Window window, final Camera camera) {
        this.camera = camera;
        this.vertexBufferFactory = new VertexBufferFactory();
        this.elementArrayBufferFactory = new ElementArrayBufferFactory();
        this.vertexArrayFactory = new VertexArrayFactory();
        this.programNametoProgram = new HashMap<>();
        this.renderQueue = renderQueue;
        this.window = window;
        this.camera = camera;
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void initialise() {
        LOG.info("Initialising renderer");
        final long startTime = System.currentTimeMillis();
        renderQueue.forEach(renderable -> renderable.initialise(this));
        LOG.info("Successfully initialised renderer in: {}ms", System.currentTimeMillis() - startTime);
    }

    public void render() {
        clearScreen();
        //Don't need to reinitialise these if nothing has changed in the camera and window. Dirty flags?
        viewMatrix = camera.getLookAt();
        projectionMatrix = new Matrix4f().ortho(0.0f, window.getWidth(), 0.0f, window.getHeight(), -1f, 1f);
        renderQueue.forEach(renderable -> renderable.prepare(this));
        renderQueue.forEach(renderable -> renderable.render(this));
        renderQueue.forEach(renderable -> renderable.after(this));
        window.swapBuffers();
    }

    private void clearScreen() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
    }

    public Renderer registerProgram(final Program program) {
        if (programNametoProgram.containsKey(program.getProgramName())) {
            throw new IllegalArgumentException("Program name: " + program.getProgramName() + " already registered to renderer!");
        }

        programNametoProgram.put(program.getProgramName(), program);
        return this;
    }

    public Program getProgram(final String programName) {
        if (!programNametoProgram.containsKey(programName)) {
            throw new IllegalStateException("Attempting to use program with name: " + programName + " but it could not be found!");
        }

        return programNametoProgram.get(programName);
    }

    public void applyProjectionMatrix(final Program program) {
        //Temporarily set view here as well TODO move
        final UniformMatrix4f viewUniform = program.getUniform("view", UniformMatrix4f.class);
        viewUniform.useUniform(viewMatrix);

        final UniformMatrix4f projectionUniform = program.getUniform("projection", UniformMatrix4f.class);
        projectionUniform.useUniform(projectionMatrix);
    }

    public StaticVertexBuffer buildNewStaticVertexBuffer(final Vertex[] vertices) {
        return vertexBufferFactory.newStaticVertexBuffer(vertices);
    }

    public ElementArrayBuffer buildNewElementArrayBuffer(final int[] indices) {
        return elementArrayBufferFactory.newElementArrayBuffer(indices);
    }

    public VertexArray buildNewVertexArray(final Buffer vertexBuffer, final Buffer elementArrayBuffer) {
        return vertexArrayFactory.newVertexArray(vertexBuffer, elementArrayBuffer);
    }
}
