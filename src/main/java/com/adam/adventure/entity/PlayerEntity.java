package com.adam.adventure.entity;

import org.joml.Vector3f;

public class PlayerEntity extends TransformableEntity {

    private final float speed;

    public PlayerEntity(final float speed) {
        this.speed = speed;
    }


    public PlayerEntity moveUp(final float deltaTime) {
        final float amountToMove = speed * deltaTime;
        final Vector3f delta = new Vector3f(0.0f, amountToMove, 0.0f);
        getTransform().translate(delta);

        return this;
    }

    public PlayerEntity moveDown(final float deltaTime) {
        final float amountToMove = speed * deltaTime;
        final Vector3f delta = new Vector3f(0.0f, -amountToMove, 0.0f);
        getTransform().translate(delta);

        return this;
    }

    public PlayerEntity moveRight(final float deltaTime) {
        final float amountToMove = speed * deltaTime;
        final Vector3f delta = new Vector3f(amountToMove, 0.0f, 0.0f);
        getTransform().translate(delta);


        return this;
    }

    public PlayerEntity moveLeft(final float deltaTime) {
        final float amountToMove = speed * deltaTime;
        final Vector3f delta = new Vector3f(-amountToMove, 0.0f, 0.0f);
        getTransform().translate(delta);

        return this;
    }

}
