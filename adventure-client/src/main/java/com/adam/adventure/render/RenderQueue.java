package com.adam.adventure.render;

import com.adam.adventure.render.renderable.Renderable;

import java.util.*;
import java.util.function.Consumer;

public class RenderQueue {
    private final Queue<Renderable> renderablesAwaitingInit;
    private final List<Renderable> renderablesToBeRendered;

    public RenderQueue() {
        this.renderablesAwaitingInit = new LinkedList<>();
        this.renderablesToBeRendered = new LinkedList<>();
    }

    /**
     * Informs the renderer that the renderable is to be initialised. The renderable should still be added to the queue
     * when needing to be rendered.
     */
    public RenderQueue initialiseRenderable(final Renderable renderable) {
        this.renderablesAwaitingInit.add(renderable);
        return this;
    }

    public RenderQueue addRenderableToBeRendered(final Renderable renderable) {
        this.renderablesToBeRendered.add(renderable);
        return this;
    }

    void forEachItemAwaitingInit(final Consumer<? super Renderable> renderableConsumer) {
        Renderable currentRenderable = renderablesAwaitingInit.poll();
        while (currentRenderable != null) {
            renderableConsumer.accept(currentRenderable);
            currentRenderable = renderablesAwaitingInit.poll();
        }
    }

    void forEach(final Consumer<? super Renderable> renderableConsumer) {
        renderablesToBeRendered.sort(Comparator.comparing(Renderable::getZIndex));
        final ListIterator<Renderable> renderableListIterator = renderablesToBeRendered.listIterator();
        while (renderableListIterator.hasNext()) {
            renderableConsumer.accept(renderableListIterator.next());
            renderableListIterator.remove();
        }
    }
}
