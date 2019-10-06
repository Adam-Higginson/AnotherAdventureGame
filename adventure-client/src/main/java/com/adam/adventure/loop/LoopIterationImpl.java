package com.adam.adventure.loop;

import com.adam.adventure.input.InputManager;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.update.UpdateStrategy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.inject.Inject;

@Slf4j
public class LoopIterationImpl implements LoopIteration {

    private final InputManager inputManager;
    private final UpdateStrategy updateStrategy;
    private final Renderer renderer;
    private long frameId;

    @Inject
    public LoopIterationImpl(final InputManager inputManager, final UpdateStrategy updateStrategy, final Renderer renderer) {
        this.inputManager = inputManager;
        this.updateStrategy = updateStrategy;
        this.renderer = renderer;
    }

    @Override
    public void onNewIteration(final long elapsedTime) {
        MDC.put("frameId", Long.toString(++frameId));
        LOG.info("Free memory: {}", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        inputManager.processInput();
        updateStrategy.update(elapsedTime);
        renderer.render();
    }
}
