package com.adam.adventure.loop;

import java.util.function.Supplier;

public class GameLoop {

    private final Supplier<Boolean> untilConditionSupplier;
    private final LoopIteration loopHandler;

    private GameLoop(final Builder builder) {
        this.untilConditionSupplier = builder.untilConditionSupplier;
        this.loopHandler = builder.loopHandler;
    }

    public void loop() {
        final long lastTime = System.currentTimeMillis();
        while (!untilConditionSupplier.get()) {
            final long currentTime = System.currentTimeMillis();
            final long elapsedTime = currentTime - lastTime;

            loopHandler.onNewIteration(elapsedTime);
        }
    }

    public static GameLoop.Builder loopUntil(final Supplier<Boolean> untilConditionSupplier) {
        return new Builder(untilConditionSupplier);
    }

    public static class Builder {
        private final Supplier<Boolean> untilConditionSupplier;
        private LoopIteration loopHandler;

        private Builder(final Supplier<Boolean> untilConditionSupplier) {
            this.untilConditionSupplier = untilConditionSupplier;
        }

        public GameLoop andUponEachLoopIterationPerform(final LoopIteration loopHandler) {
            this.loopHandler = loopHandler;
            return new GameLoop(this);
        }
    }
}

