package com.adam.adventure.render.camera.vertex;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class VertexArray {
    private static final int NULL_VERTEX_ARRAY = 0;

    private final int vertexArrayId;
    private final int numberOfElements;

    VertexArray(final int vertexArrayId, final int numberOfElements) {
        this.vertexArrayId = vertexArrayId;
        this.numberOfElements = numberOfElements;
    }

    public void enableVertexArray() {
        glBindVertexArray(vertexArrayId);
    }

    public void draw() {
        glDrawElements(GL_TRIANGLES, numberOfElements, GL_UNSIGNED_INT, 0);
    }

    public void unbind() {
        glBindVertexArray(NULL_VERTEX_ARRAY);
    }
}
