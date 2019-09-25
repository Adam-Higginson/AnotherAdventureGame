package com.adam.adventure.render.vertex;


import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class VertexArrayFactory {


    public VertexArray newVertexArray(final Buffer vertexBuffer, final Buffer elementArrayBuffer) {
        final int vertexArrayId = glGenVertexArrays();
        glBindVertexArray(vertexArrayId);

        vertexBuffer.bindBufferData();
        elementArrayBuffer.bindBufferData();
        glVertexAttribPointer(0, 3, GL_FLOAT, false, Vertex.STRIDE, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, Vertex.STRIDE, Vertex.TEXTURE_OFFSET);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //Unbind
        glBindVertexArray(0);

        return new VertexArray(vertexArrayId, elementArrayBuffer.getNumberOfElements());
    }

    /**
     * Returns a vertex array which contains no {@link Vertex} data, but instead simply draws vertices with no data.
     * This is useful in the case where you want to generate vertices in the shader.
     *
     * @param numberOfElements How many elements to draw.
     * @return The empty vertex array.
     */
    public VertexArray newEmptyVertexArray(final int numberOfElements) {
        final int vertexArrayId = glGenVertexArrays();
        return new VertexArray(vertexArrayId, numberOfElements);
    }
}
