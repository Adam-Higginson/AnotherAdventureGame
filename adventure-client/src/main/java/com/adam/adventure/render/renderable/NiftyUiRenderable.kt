package com.adam.adventure.render.renderable

import com.adam.adventure.render.Renderer
import de.lessvoid.nifty.Nifty

class NiftyUiRenderable(val nifty : Nifty) : Renderable {
    override fun getZIndex(): Int {
        return 0;
    }


    override fun render(renderer: Renderer?) {
        nifty.render(false)
    }
}