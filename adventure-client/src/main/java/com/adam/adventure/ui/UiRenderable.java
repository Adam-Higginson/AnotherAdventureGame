package com.adam.adventure.ui;

import com.adam.adventure.render.Renderer;
import com.adam.adventure.render.renderable.Renderable;
import de.lessvoid.nifty.Nifty;

import javax.inject.Inject;

public class UiRenderable implements Renderable {

    private final Nifty nifty;

    @Inject
    public UiRenderable(final Nifty nifty) {
        this.nifty = nifty;
    }

    @Override
    public int getZIndex() {
        //Doesn't matter
        return 0;
    }

    @Override
    public void initialise(final Renderer renderer) {
        // Nothing needs doing
    }

    @Override
    public void prepare(final Renderer renderer) {
        // Nothing needs doing
    }

    @Override
    public void render(final Renderer renderer) {
        nifty.render(false);
    }

    @Override
    public void after(final Renderer renderer) {
        // Nothing needs doing
    }
}
