package com.adam.adventure.render.ui;

import com.adam.adventure.render.Renderer;
import com.adam.adventure.render.renderable.Renderable;
import de.lessvoid.nifty.Nifty;

public class NiftyUiRenderable implements Renderable {

    private final Nifty nifty;

    NiftyUiRenderable(final Nifty nifty) {
        this.nifty = nifty;
    }

    @Override
    public int getZIndex() {
        //Always at the top
        return Integer.MAX_VALUE;
    }

    @Override
    public void initialise(final Renderer renderer) {
        //Nothing needs doing
    }

    @Override
    public void prepare(final Renderer renderer) {
        nifty.update();
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
