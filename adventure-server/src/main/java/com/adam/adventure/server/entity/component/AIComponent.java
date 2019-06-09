package com.adam.adventure.server.entity.component;

import com.adam.adventure.entity.EntityComponent;
import org.joml.Vector3f;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AIComponent extends EntityComponent {
    private static final long TIME_TO_MOVE = 1000;
    /**
     * The range in which the target can differ.
     */
    private static final int TARGET_RANGE = 50;

    private final float speed;
    private final Random random;
    private final Vector3f initialTranslation;
    private final Vector3f target;

    private long lastTimeSwitched;

    public AIComponent(final float speed) {
        this.speed = speed;
        this.random = new Random(System.currentTimeMillis());
        this.initialTranslation = new Vector3f();
        this.target = new Vector3f();
    }

    @Override
    protected void activate() {
        getTransformComponent().getTransform().getTranslation(initialTranslation);
        regenerateTarget();
    }

    @Override
    protected void update(final float deltaTime) {
        if (System.currentTimeMillis() - lastTimeSwitched > TIME_TO_MOVE) {
            regenerateTarget();
        }

//        if (direction) {
//            moveUp(deltaTime);
//            broadcastComponentEvent(new MovementComponentEvent(MovementComponentEvent.MovementType.ENTITY_MOVE_NORTH));
//        } else {
//            moveDown(deltaTime);
//            broadcastComponentEvent(new MovementComponentEvent(MovementComponentEvent.MovementType.ENTITY_MOVE_SOUTH));
//        }
    }

    private void regenerateTarget() {
        lastTimeSwitched = System.currentTimeMillis();
        final float nextX = ThreadLocalRandom.current()
                .nextInt((int) initialTranslation.x - TARGET_RANGE, (int) initialTranslation.x + TARGET_RANGE);
        final float nextY = ThreadLocalRandom.current()
                .nextInt((int) initialTranslation.y - TARGET_RANGE, (int) initialTranslation.y + TARGET_RANGE);

        target.x = nextX;
        target.y = nextY;
        target.z = initialTranslation.z;
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
