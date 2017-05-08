package com.adam.adventure.render.camera;

import com.adam.adventure.render.camera.renderable.Renderable;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class RenderQueue {
    private final Queue<Renderable> renderableQueue;

    public RenderQueue() {
        renderableQueue = new LinkedList<>();
    }

    public RenderQueue addRenderable(final Renderable renderable) {
        renderableQueue.add(renderable);
        return this;
    }

    public void forEach(final Consumer<? super Renderable> renderableConsumer) {
        renderableQueue.forEach(renderableConsumer);
    }
}
