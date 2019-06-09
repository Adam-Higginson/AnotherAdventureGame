package com.adam.adventure.server.entity.component;

import com.adam.adventure.entity.EntityComponent;
import org.joml.Vector3f;

public class AIComponent extends EntityComponent {
    private static final long TIME_TO_MOVE = 1000;

    private final float speed;
    private long lastTimeSwitched;
    private boolean direction;

    public AIComponent(final float speed) {
        this.speed = speed;
    }

    @Override
    protected void activate() {
        lastTimeSwitched = System.currentTimeMillis();
    }

    @Override
    protected void update(final float deltaTime) {
        if (System.currentTimeMillis() - lastTimeSwitched > TIME_TO_MOVE) {
            direction = !direction;
            lastTimeSwitched = System.currentTimeMillis();
        }

        if (direction) {
            moveUp(deltaTime);
        } else {
            moveDown(deltaTime);
        }

    }

    private void moveUp(final float elapsedTime) {
        final float amountToMove = speed * elapsedTime;
        final Vector3f delta = new Vector3f(0.0f, amountToMove, 0.0f);
        getTransformComponent().getTransform().translate(delta);
    }

    private void moveDown(final float elapsedTime) {
        final float amountToMove = speed * elapsedTime;
        final Vector3f delta = new Vector3f(0.0f, -amountToMove, 0.0f);
        getTransformComponent().getTransform().translate(delta);
    }

}
