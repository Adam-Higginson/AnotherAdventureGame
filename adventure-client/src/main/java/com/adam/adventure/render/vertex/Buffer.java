package com.adam.adventure.render.vertex;

public interface Buffer {
    void bindBufferData();

    int getNumberOfElements();

    void delete();
}
