package com.adam.adventure.loop;

import com.adam.adventure.input.InputManager;
import com.adam.adventure.input.KeyPressListener;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.update.UpdateStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;

public class DebugLoopIterationImpl implements LoopIteration, KeyPressListener {
    private static final Logger LOG = LoggerFactory.getLogger(DebugLoopIterationImpl.class);

    private final InputManager inputManager;
    private final UpdateStrategy updateStrategy;
    private final Renderer renderer;
    private CountDownLatch countDown;

    @Inject
    public DebugLoopIterationImpl(final InputManager inputManager, final UpdateStrategy updateStrategy, final Renderer renderer) {
        this.inputManager = inputManager;
        this.updateStrategy = updateStrategy;
        this.renderer = renderer;
        this.countDown = new CountDownLatch(1);
        this.inputManager.addKeyPressListener(this);
    }


    @Override
    public void onNewIteration(final long elapsedTime) {
        inputManager.processInput();

        try {
            final boolean countedDown = countDown.await(10, TimeUnit.MILLISECONDS);
            if (countedDown) {
                updateStrategy.update(50);
                renderer.render();
                this.countDown = new CountDownLatch(1);
            }

        } catch (final InterruptedException e) {
            LOG.error("Interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onKeyPress(final int key) {
        if (key == GLFW_KEY_F2) {
            countDown.countDown();
        }
    }
}
