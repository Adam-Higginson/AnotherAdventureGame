package com.adam.adventure.render.camera.renderable;

import com.adam.adventure.render.camera.Renderer;

public interface Renderable {
    void initialise(final Renderer renderer);

    void prepare(final Renderer renderer);

    void render(final Renderer renderer);

    void after(final Renderer renderer);
}
