package com.adam.adventure.loop;

import com.adam.adventure.input.InputManager;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.update.UpdateStrategy;

public class LoopIterationImpl implements LoopIteration {

    private final InputManager inputManager;
    private final UpdateStrategy updateStrategy;
    private final Renderer renderer;

    public LoopIterationImpl(final InputManager inputManager, final UpdateStrategy updateStrategy, final Renderer renderer) {
        this.inputManager = inputManager;
        this.updateStrategy = updateStrategy;
        this.renderer = renderer;
    }

    @Override
    public void onNewIteration(final long elapsedTime) {
        inputManager.processInput();
        updateStrategy.update(elapsedTime);
        renderer.render();
    }
}
