package com.adam.adventure.render.camera.renderable;

import com.adam.adventure.entity.TileEntity;
import com.adam.adventure.render.camera.RenderableEntity;
import com.adam.adventure.render.camera.Renderer;
import com.adam.adventure.render.camera.shader.Program;
import com.adam.adventure.render.camera.shader.Uniform4f;
import com.adam.adventure.render.camera.shader.UniformMatrix4f;
import com.adam.adventure.render.camera.vertex.ElementArrayBuffer;
import com.adam.adventure.render.camera.vertex.StaticVertexBuffer;
import com.adam.adventure.render.camera.vertex.Vertex;
import com.adam.adventure.render.camera.vertex.VertexArray;
import org.joml.Matrix4f;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class TileRenderable extends RenderableEntity<TileEntity> {

    private VertexArray vertexArray;

    public TileRenderable(final TileEntity entity) {
        super(entity);
    }

    @Override
    public void initialise(final Renderer renderer) {
//        final Vertex[] vertices = new Vertex[]{
//                Vertex.of(0.5f, 0.5f), // Top Right
//                Vertex.of(0.5f, -0.5f), // Bottom Right
//                Vertex.of(-0.5f, -0.5f),  // Bottom Left
//                Vertex.of(-0.5f, 0.5f)   // Top Left
//        };

        final Vertex[] vertices = new Vertex[]{
                Vertex.of(50f, 50f), // Top Right
                Vertex.of(50f, -50f), // Bottom Right
                Vertex.of(-50f, -50f),  // Bottom Left
                Vertex.of(-50f, 50f)   // Top Left
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
    public void prepare(final Renderer renderer) {
        final float greenValue = (float) (Math.sin(glfwGetTime()) / 2) + 0.5f;
        final float blueValue = (float) (Math.cos(glfwGetTime()) / 2) + 0.5f;
        getEntity().getTransform().translate(0.5f, -0.5f, 0.0f)
                .rotate((float) Math.toRadians(glfwGetTime() * 50f), 0.0f, 0.0f, 1.0f);

        final Program program = renderer.getProgram("Test Program");
        final Uniform4f someColourUniform = program.getUniform("someColour", Uniform4f.class);
        program.useProgram();
        someColourUniform.useUniform(blueValue, greenValue, 0.0f, 1.0f);

        final UniformMatrix4f modelUniform = program.getUniform("model", UniformMatrix4f.class);
        final Matrix4f transformMatrix = getEntity().getTransform();
        modelUniform.useUniform(transformMatrix);

        renderer.applyProjectionMatrix(program);
    }

    @Override
    public void render(final Renderer renderer) {
        vertexArray.enableVertexArray();
        vertexArray.draw();
    }

    @Override
    public void after(final Renderer renderer) {
        vertexArray.unbind();
        getEntity().getTransform().identity();
    }
}
