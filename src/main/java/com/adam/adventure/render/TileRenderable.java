package com.adam.adventure.render;

import com.adam.adventure.entity.TileEntity;
import com.adam.adventure.render.shader.Program;
import com.adam.adventure.render.shader.Uniform4f;
import com.adam.adventure.render.vertex.ElementArrayBuffer;
import com.adam.adventure.render.vertex.StaticVertexBuffer;
import com.adam.adventure.render.vertex.Vertex;
import com.adam.adventure.render.vertex.VertexArray;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class TileRenderable extends RenderableEntity<TileEntity> {

    private VertexArray vertexArray;

    public TileRenderable(final TileEntity entity) {
        super(entity);
    }

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
        final float greenValue = (float) (Math.sin(glfwGetTime()) / 2) + 0.5f;
        final float blueValue = (float) (Math.cos(glfwGetTime()) / 2) + 0.5f;


        final Program program = renderer.getProgram("Test Program");
        final Uniform4f someColourUniform = program.getUniform("someColour", Uniform4f.class);
        program.useProgram();
        someColourUniform.useUniform(blueValue, greenValue, 0.0f, 1.0f);

        vertexArray.enableVertexArray();
    }
}
