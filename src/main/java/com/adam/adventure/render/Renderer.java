package com.adam.adventure.render;

import com.adam.adventure.render.shader.Program;
import com.adam.adventure.render.vertex.*;
import com.adam.adventure.window.Window;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private final VertexBufferFactory vertexBufferFactory;
    private final ElementArrayBufferFactory elementArrayBufferFactory;
    private final VertexArrayFactory vertexArrayFactory;
    private final Map<String, Program> programNametoProgram;
    private final RenderQueue renderQueue;
    private final Window window;

    public Renderer(final RenderQueue renderQueue, final Window window) {
        this.vertexBufferFactory = new VertexBufferFactory();
        this.elementArrayBufferFactory = new ElementArrayBufferFactory();
        this.vertexArrayFactory = new VertexArrayFactory();
        this.programNametoProgram = new HashMap<>();
        this.renderQueue = renderQueue;
        this.window = window;
    }


    public void render() {
        clearScreen();
        renderQueue.forEach(renderable -> renderable.render(this));
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

    public StaticVertexBuffer buildNewStaticVertexBuffer(final Vertex[] vertices) {
        return vertexBufferFactory.newStaticVertexBuffer(vertices);
    }

    public ElementArrayBuffer buildNewElementArrayBuffer(final int[] indices) {
        return elementArrayBufferFactory.newElementArrayBuffer(indices);
    }

    public VertexArray buildNewVertexArray(final Buffer vertexBuffer, final ElementArrayBuffer elementArrayBuffer) {
        return vertexArrayFactory.newVertexArray(vertexBuffer, elementArrayBuffer);
    }


    public <T extends Renderable> T buildRenderable(final Supplier<T> renderableSupplier) {
        final T renderable = renderableSupplier.get();
        renderable.initialise(this);
        return renderable;
    }
}
