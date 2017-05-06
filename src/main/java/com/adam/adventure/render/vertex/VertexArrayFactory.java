package com.adam.adventure.render.vertex;


import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class VertexArrayFactory {

    public VertexArray newVertexArray(final Buffer vertexBuffer, final ElementArrayBuffer elementArrayBuffer) {
        final int vertexArrayId = glGenVertexArrays();
        glBindVertexArray(vertexArrayId);

        vertexBuffer.bindBufferData();
        elementArrayBuffer.bindBufferData();
        glVertexAttribPointer(0, Vertex.NUM_ELEMENTS_PER_VERTEX, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        //Unbind
        glBindVertexArray(0);

        return new VertexArray(vertexArrayId, elementArrayBuffer.getNumberOfElements());
    }
}
