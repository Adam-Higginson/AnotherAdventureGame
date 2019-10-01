package com.adam.adventure.render.renderable;

import com.adam.adventure.entity.Entity;
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

public class TileRenderable extends RenderableEntity<Entity> {

    private VertexArray vertexArray;
    private final Texture texture;

    public TileRenderable(final Entity entity, final Texture texture) {
        super(entity);
        this.texture = texture;
    }

    @Override
    public int getZIndex() {
        return 0;
    }

    @Override
    public void initialise(final Renderer renderer) {
        final Vertex[] vertices = new Vertex[]{
                Vertex.of(50f, 50f, 16f, 16f), // Top Right
                Vertex.of(50f, -50f, 16f, 0f), // Bottom Right
                Vertex.of(-50f, -50f, 0f, 0f),  // Bottom Left
                Vertex.of(-50f, 50f, 0f, 16f)   // Top Left
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
    }

    @Override
    public void render(final Renderer renderer) {
        final Program program = renderer.getProgram("Test Program");
        program.useProgram();
        applyUniforms(program);

        renderer.applyProjectionMatrix(program);

        texture.bind(0);
        vertexArray.enableVertexArray();
        vertexArray.draw();

        program.disableProgram();
        vertexArray.unbind();
        vertexArray.unbind();
    }

    @Override
    public void destroy() {
        texture.destroy();
    }

    private void applyUniforms(final Program program) {
        final UniformMatrix4f modelUniform = program.getUniform("model", UniformMatrix4f.class);
        final Matrix4f transformMatrix = getEntity().getTransform();
        modelUniform.useUniform(transformMatrix);

        final Uniform2f textureDimensionsUniform = program.getUniform("textureDimensions", Uniform2f.class);
        textureDimensionsUniform.useUniform(texture.getWidth(), texture.getHeight());

        final Uniform2f textureOffset = program.getUniform("textureOffset", Uniform2f.class);
        textureOffset.useUniform(0, 0);
    }

    @Override
    public void after(final Renderer renderer) {
        getEntity().getTransform().identity();
    }
}
