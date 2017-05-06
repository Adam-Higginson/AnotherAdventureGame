package com.adam.adventure.render;

import com.adam.adventure.render.vertex.StaticVertexBuffer;
import com.adam.adventure.render.vertex.Vertex;

public class TileRenderable implements Renderable {

    private final Vertex[] vertices;

    public TileRenderable() {
        vertices = new Vertex[]{
                Vertex.of(-0.5f, -0.5f),
                Vertex.of(0.5f, -0.5f),
                Vertex.of(0.0f, 0.5f)
        };
    }

    @Override
    public void initialise(final Renderer renderer) {
        final StaticVertexBuffer vertexBuffer = renderer.buildNewStaticVertexBuffer(vertices);
    }

    @Override
    public void render(final Renderer renderer) {
        //TODO
    }
}
