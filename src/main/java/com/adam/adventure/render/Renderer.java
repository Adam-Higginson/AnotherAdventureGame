package com.adam.adventure.render;

import com.adam.adventure.render.vertex.StaticVertexBuffer;
import com.adam.adventure.render.vertex.Vertex;
import com.adam.adventure.render.vertex.VertexBufferFactory;
import com.adam.adventure.window.Window;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private final VertexBufferFactory vertexBufferFactory;
    private final RenderQueue renderQueue;
    private final Window window;

    public Renderer(final RenderQueue renderQueue, final Window window) {
        this.vertexBufferFactory = new VertexBufferFactory();
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


    public StaticVertexBuffer buildNewStaticVertexBuffer(final Vertex[] vertices) {
        return vertexBufferFactory.newStaticVertexBuffer(vertices);
    }

    public <T extends Renderable> T buildRenderable(final Supplier<T> renderableSupplier) {
        final T renderable = renderableSupplier.get();
        renderable.initialise(this);
        return renderable;
    }
}
