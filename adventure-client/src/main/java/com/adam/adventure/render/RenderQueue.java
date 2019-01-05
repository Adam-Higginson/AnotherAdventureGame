package com.adam.adventure.render;

import com.adam.adventure.render.renderable.Renderable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class RenderQueue {
    private final List<Renderable> renderableQueue;

    public RenderQueue() {
        renderableQueue = new ArrayList<>();
    }

    public RenderQueue addRenderable(final Renderable renderable) {
        renderableQueue.add(renderable);
        return this;
    }

    void prepareForRetrieval() {
        renderableQueue.sort(Comparator.comparing(Renderable::getZIndex));
    }

    void forEach(final Consumer<? super Renderable> renderableConsumer) {
        renderableQueue.forEach(renderableConsumer);
    }
}
