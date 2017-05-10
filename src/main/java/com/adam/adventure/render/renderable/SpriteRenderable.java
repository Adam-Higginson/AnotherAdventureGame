package com.adam.adventure.render.renderable;

import com.adam.adventure.entity.TransformableEntity;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.render.shader.Program;
import com.adam.adventure.render.shader.Uniform2f;
import com.adam.adventure.render.shader.UniformMatrix4f;
import com.adam.adventure.render.texture.Texture;
import com.adam.adventure.render.vertex.ElementArrayBuffer;
import com.adam.adventure.render.vertex.StaticVertexBuffer;
import com.adam.adventure.render.vertex.Vertex;
import com.adam.adventure.render.vertex.VertexArray;
import org.joml.Matrix4f;

public class SpriteRenderable extends RenderableEntity<TransformableEntity> {

    private VertexArray vertexArray;
    private final Texture spriteTexture;

    public SpriteRenderable(final TransformableEntity entity, final Texture spriteTexture) {
        super(entity);
        this.spriteTexture = spriteTexture;
    }

    @Override
    public void initialise(final Renderer renderer) {
        final Vertex[] vertices = new Vertex[]{
                Vertex.of(64f, 64f, 96f, 96f), // Top Right
                Vertex.of(64f, 0f, 96f, 0f), // Bottom Right
                Vertex.of(0f, 0f, 0.0f, 0f),  // Bottom Left
                Vertex.of(0f, 64f, 0.0f, 96f)   // Top Left
        };

        final int[] indices = {
                0, 1, 3,   // First Triangle
                1, 2, 3    // Second Triangle
        };

        final StaticVertexBuffer vertexBuffer = renderer.buildNewStaticVertexBuffer(vertices);
        final ElementArrayBuffer elementArrayBuffer = renderer.buildNewElementArrayBuffer(indices);
        vertexArray = renderer.buildNewVertexArray(vertexBuffer, elementArrayBuffer);
    }

    @Override
    public void prepare(final Renderer renderer) {
    }

    @Override
    public void render(final Renderer renderer) {
        final Program program = renderer.getProgram("Test Program");
        program.useProgram();
        applyUniforms(program);
        renderer.applyProjectionMatrix(program);

        spriteTexture.bindTexture();
        vertexArray.enableVertexArray();
        vertexArray.draw();
    }

    private void applyUniforms(final Program program) {
        final UniformMatrix4f modelUniform = program.getUniform("model", UniformMatrix4f.class);
        final Matrix4f transformMatrix = getEntity().getTransform();
        modelUniform.useUniform(transformMatrix);

        final Uniform2f textureDimensionsUniform = program.getUniform("textureDimensions", Uniform2f.class);
        textureDimensionsUniform.useUniform(spriteTexture.getWidth(), spriteTexture.getHeight());
    }

    @Override
    public void after(final Renderer renderer) {
        vertexArray.unbind();
    }
}
