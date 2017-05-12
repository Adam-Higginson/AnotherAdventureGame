package com.adam.adventure.render.renderable;

import com.adam.adventure.entity.SpriteEntity;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.render.shader.Program;
import com.adam.adventure.render.shader.Uniform2f;
import com.adam.adventure.render.shader.UniformMatrix4f;
import com.adam.adventure.render.vertex.ElementArrayBuffer;
import com.adam.adventure.render.vertex.StaticVertexBuffer;
import com.adam.adventure.render.vertex.Vertex;
import com.adam.adventure.render.vertex.VertexArray;
import org.joml.Matrix4f;

public class SpriteRenderable extends RenderableEntity<SpriteEntity> {

    private VertexArray vertexArray;
    private final int zIndex;

    public SpriteRenderable(final SpriteEntity entity, final int zIndex) {
        super(entity);
        this.zIndex = zIndex;
    }


    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void initialise(final Renderer renderer) {

        final float texTopRightX = getEntity().getSprite().getTextureOffset().getX() + getEntity().getSprite().getTextureOffset().getWidth();
        final float texTopRightY = getEntity().getSprite().getTextureOffset().getY() + getEntity().getSprite().getTextureOffset().getHeight();
        final float texBottomRightX = getEntity().getSprite().getTextureOffset().getX() + getEntity().getSprite().getTextureOffset().getWidth();
        final float texBottomRightY = getEntity().getSprite().getTextureOffset().getY();
        final float texBottomLeftX = getEntity().getSprite().getTextureOffset().getX();
        final float texBottomLeftY = getEntity().getSprite().getTextureOffset().getY();
        final float texTopLeftX = getEntity().getSprite().getTextureOffset().getX();
        final float textTopLeftY = getEntity().getSprite().getTextureOffset().getY() + getEntity().getSprite().getTextureOffset().getHeight();

        final Vertex[] vertices = new Vertex[]{
                Vertex.of(getEntity().getSprite().getWidth(), getEntity().getSprite().getHeight(), texTopRightX, texTopRightY), // Top Right
                Vertex.of(getEntity().getSprite().getWidth(), 0f, texBottomRightX, texBottomRightY), // Bottom Right
                Vertex.of(0f, 0f, texBottomLeftX, texBottomLeftY),  // Bottom Left
                Vertex.of(0f, getEntity().getSprite().getHeight(), texTopLeftX, textTopLeftY)   // Top Left
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

        getEntity().getSprite().getTexture().bindTexture();
        vertexArray.enableVertexArray();
        vertexArray.draw();
    }

    private void applyUniforms(final Program program) {
        final UniformMatrix4f modelUniform = program.getUniform("model", UniformMatrix4f.class);
        final Matrix4f transformMatrix = getEntity().getTransform();
        modelUniform.useUniform(transformMatrix);

        final Uniform2f textureDimensionsUniform = program.getUniform("textureDimensions", Uniform2f.class);
        textureDimensionsUniform.useUniform(getEntity().getSprite().getTexture().getWidth(),
                getEntity().getSprite().getTexture().getHeight());

        final Uniform2f textureOffset = program.getUniform("textureOffset", Uniform2f.class);
        textureOffset.useUniform(getEntity().getSprite().getTextureOffset().getX(),
                getEntity().getSprite().getTextureOffset().getY());
    }

    @Override
    public void after(final Renderer renderer) {
        vertexArray.unbind();
    }
}
