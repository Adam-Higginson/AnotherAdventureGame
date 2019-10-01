package com.adam.adventure.render.renderable;

import com.adam.adventure.render.Renderer;

public interface Renderable {
    int getZIndex();

    default void initialise(final Renderer renderer) {
        // Do nothing
    }

    default void prepare(final Renderer renderer) {
        // Do nothing
    }

    default void render(final Renderer renderer) {
        // Do nothing
    }

    default void after(final Renderer renderer) {
        // Do nothing
    }

    /**
     * Called when the renderable is requested to be destroyed
     */
    default void destroy() {
        // Do nothing
    }
}
