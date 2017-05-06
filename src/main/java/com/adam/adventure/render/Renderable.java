package com.adam.adventure.render;

public interface Renderable {
    void initialise(final Renderer renderer);

    void render(final Renderer renderer);
}
