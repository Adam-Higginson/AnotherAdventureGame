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
}
