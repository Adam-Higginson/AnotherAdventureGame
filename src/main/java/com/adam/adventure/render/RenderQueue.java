package com.adam.adventure.render;

import com.adam.adventure.render.renderable.Renderable;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;

public class RenderQueue {
    private final SortedSet<Renderable> renderableQueue;

    public RenderQueue() {
        renderableQueue = new TreeSet<>(Comparator.comparing(Renderable::getZIndex));
    }

    public RenderQueue addRenderable(final Renderable renderable) {
        renderableQueue.add(renderable);
        return this;
    }

    public void forEach(final Consumer<? super Renderable> renderableConsumer) {
        renderableQueue.forEach(renderableConsumer);
    }
}
