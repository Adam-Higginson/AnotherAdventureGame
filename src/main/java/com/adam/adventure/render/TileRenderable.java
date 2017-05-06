package com.adam.adventure.render;

import com.adam.adventure.render.vertex.ElementArrayBuffer;
import com.adam.adventure.render.vertex.StaticVertexBuffer;
import com.adam.adventure.render.vertex.Vertex;
import com.adam.adventure.render.vertex.VertexArray;

public class TileRenderable implements Renderable {

    private VertexArray vertexArray;

    @Override
    public void initialise(final Renderer renderer) {
        final Vertex[] vertices = new Vertex[]{
                Vertex.of(0.5f, 0.5f), // Top Right
                Vertex.of(0.5f, -0.5f), // Bottom Right
                Vertex.of(-0.5f, -0.5f),  // Bottom Left
                Vertex.of(-0.5f, 0.5f)   // Top Left
        };

        final int[] indices = { // Note that we start from 0!
                0, 1, 3,   // First Triangle
                1, 2, 3    // Second Triangle
        };

        final StaticVertexBuffer vertexBuffer = renderer.buildNewStaticVertexBuffer(vertices);
        final ElementArrayBuffer elementArrayBuffer = renderer.buildNewElementArrayBuffer(indices);
        vertexArray = renderer.buildNewVertexArray(vertexBuffer, elementArrayBuffer);
    }

    @Override
    public void render(final Renderer renderer) {
        vertexArray.enableVertexArray();
    }
}
