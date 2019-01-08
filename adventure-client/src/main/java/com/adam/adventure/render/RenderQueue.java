package com.adam.adventure.render;

import com.adam.adventure.module.Timed;
import com.adam.adventure.render.renderable.Renderable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

public class RenderQueue {
    private final PriorityQueue<Renderable> renderablesAwaitingInit;
    private final PriorityQueue<Renderable> renderablesToBeRendered;

    public RenderQueue() {
        this.renderablesAwaitingInit = new PriorityQueue<>(Comparator.comparing(Renderable::getZIndex));
        this.renderablesToBeRendered = new PriorityQueue<>(Comparator.comparing(Renderable::getZIndex));
    }

    public RenderQueue addRenderable(final Renderable renderable) {
        this.renderablesAwaitingInit.add(renderable);
        return this;
    }

    void forEachItemAwaitingInit(final Consumer<? super Renderable> renderableConsumer) {
        Renderable currentRenderable = renderablesAwaitingInit.poll();
        while (currentRenderable != null) {
            renderableConsumer.accept(currentRenderable);
            renderablesToBeRendered.offer(currentRenderable);
            currentRenderable = renderablesAwaitingInit.poll();
        }
    }

    void forEach(final Consumer<? super Renderable> renderableConsumer) {
        final List<Renderable> drainedEntities = new ArrayList<>(renderablesToBeRendered.size());
        Renderable currentRenderable = renderablesToBeRendered.poll();
        while (currentRenderable != null) {
            renderableConsumer.accept(currentRenderable);
            drainedEntities.add(currentRenderable);
            currentRenderable = renderablesToBeRendered.poll();
        }

        renderablesToBeRendered.addAll(drainedEntities);
    }
}
