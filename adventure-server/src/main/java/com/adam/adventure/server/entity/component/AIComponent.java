package com.adam.adventure.server.entity.component;

import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.entity.component.event.MovementComponentEvent;
import org.joml.Random;
import org.joml.Vector3f;

public class AIComponent extends EntityComponent {
    private static final long TIME_TO_MOVE = 1000;
    /**
     * The range in which the target can differ.
     */
    private static final int TARGET_RANGE = 50;

    private final float speed;
    private final Random random;
    private final Vector3f initialTranslation;
    private boolean direction;

    private long lastTimeSwitched;
    private Vector3f target;

    public AIComponent(final float speed) {
        this.speed = speed;
        this.random = new Random(System.currentTimeMillis());
        this.initialTranslation = new Vector3f();
    }

    @Override
    protected void activate() {
        lastTimeSwitched = System.currentTimeMillis();
        getTransformComponent().getTransform().getTranslation(initialTranslation);
    }

    @Override
    protected void update(final float deltaTime) {
        if (System.currentTimeMillis() - lastTimeSwitched > TIME_TO_MOVE) {
            direction = !direction;
            lastTimeSwitched = System.currentTimeMillis();
        }

        if (direction) {
            moveUp(deltaTime);
            broadcastComponentEvent(new MovementComponentEvent(MovementComponentEvent.MovementType.ENTITY_MOVE_NORTH));
        } else {
            moveDown(deltaTime);
            broadcastComponentEvent(new MovementComponentEvent(MovementComponentEvent.MovementType.ENTITY_MOVE_SOUTH));
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
