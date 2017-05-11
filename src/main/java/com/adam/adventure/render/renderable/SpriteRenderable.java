package com.adam.adventure.render.renderable;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.render.Sprite;
import com.adam.adventure.render.shader.Program;
import com.adam.adventure.render.shader.Uniform2f;
import com.adam.adventure.render.shader.UniformMatrix4f;
import com.adam.adventure.render.vertex.ElementArrayBuffer;
import com.adam.adventure.render.vertex.StaticVertexBuffer;
import com.adam.adventure.render.vertex.Vertex;
import com.adam.adventure.render.vertex.VertexArray;
import org.joml.Matrix4f;

public class SpriteRenderable extends RenderableEntity<Entity> {

    private VertexArray vertexArray;
    private final Sprite sprite;
    private final int zIndex;

    public SpriteRenderable(final Entity entity, final Sprite sprite, final int zIndex) {
        super(entity);
        this.sprite = sprite;
        this.zIndex = zIndex;
    }


    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void initialise(final Renderer renderer) {

        final float texTopRightX = sprite.getTextureOffset().getX() + sprite.getTextureOffset().getWidth();
        final float texTopRightY = sprite.getTextureOffset().getY() + sprite.getTextureOffset().getHeight();
        final float texBottomRightX = sprite.getTextureOffset().getX() + sprite.getTextureOffset().getWidth();
        final float texBottomRightY = sprite.getTextureOffset().getY();
        final float texBottomLeftX = sprite.getTextureOffset().getX();
        final float texBottomLeftY = sprite.getTextureOffset().getY();
        final float texTopLeftX = sprite.getTextureOffset().getX();
        final float textTopLeftY = sprite.getTextureOffset().getY() + sprite.getTextureOffset().getHeight();

        final Vertex[] vertices = new Vertex[]{
                Vertex.of(sprite.getWidth(), sprite.getHeight(), texTopRightX, texTopRightY), // Top Right
                Vertex.of(sprite.getWidth(), 0f, texBottomRightX, texBottomRightY), // Bottom Right
                Vertex.of(0f, 0f, texBottomLeftX, texBottomLeftY),  // Bottom Left
                Vertex.of(0f, sprite.getHeight(), texTopLeftX, textTopLeftY)   // Top Left
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

        sprite.getTexture().bindTexture();
        vertexArray.enableVertexArray();
        vertexArray.draw();
    }

    private void applyUniforms(final Program program) {
        final UniformMatrix4f modelUniform = program.getUniform("model", UniformMatrix4f.class);
        final Matrix4f transformMatrix = getEntity().getTransform();
        modelUniform.useUniform(transformMatrix);

        final Uniform2f textureDimensionsUniform = program.getUniform("textureDimensions", Uniform2f.class);
        textureDimensionsUniform.useUniform(sprite.getTexture().getWidth(), sprite.getTexture().getHeight());

        final Uniform2f textureOffset = program.getUniform("textureOffset", Uniform2f.class);
        textureOffset.useUniform(sprite.getTextureOffset().getX(), sprite.getTextureOffset().getWidth());
    }

    @Override
    public void after(final Renderer renderer) {
        vertexArray.unbind();
    }
}
