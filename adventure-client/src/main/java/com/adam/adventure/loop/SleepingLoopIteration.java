package com.adam.adventure.loop;

import com.adam.adventure.input.InputManager;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.update.UpdateStrategy;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@Slf4j
public class SleepingLoopIteration extends LoopIterationImpl {
    private static final long TARGET_MS_PER_FRAME = 1000 / 60;

    @Inject
    public SleepingLoopIteration(final InputManager inputManager, final UpdateStrategy updateStrategy, final Renderer renderer) {
        super(inputManager, updateStrategy, renderer);
    }

    @Override
    public void onNewIteration(final long elapsedTime) {
        final long startTime = System.currentTimeMillis();
        super.onNewIteration(elapsedTime);
        final long duration = System.currentTimeMillis() - startTime;

        if (duration < TARGET_MS_PER_FRAME) {
            try {
                Thread.sleep(TARGET_MS_PER_FRAME - duration);
            } catch (final InterruptedException e) {
                LOG.info("Interrupted in game loop");
                Thread.currentThread().interrupt();
            }
        }
    }
}
