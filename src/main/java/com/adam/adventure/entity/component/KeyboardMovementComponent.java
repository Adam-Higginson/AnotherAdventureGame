package com.adam.adventure.entity.component;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.input.InputManager;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardMovementComponent implements EntityComponent {

    private final float speed;
    private final InputManager inputManager;

    public KeyboardMovementComponent(final float speed, final InputManager inputManager) {
        this.speed = speed;
        this.inputManager = inputManager;
    }


    @Override
    public void update(final Entity target, final float elapsedTime) {
        if (inputManager.isKeyPressed(GLFW_KEY_W)) {
            moveUp(target, elapsedTime);
        }
        if (inputManager.isKeyPressed(GLFW_KEY_S)) {
            moveDown(target, elapsedTime);
        }
        if (inputManager.isKeyPressed(GLFW_KEY_A)) {
            moveLeft(target, elapsedTime);
        }
        if (inputManager.isKeyPressed(GLFW_KEY_D)) {
            moveRight(target, elapsedTime);
        }
    }


    private void moveUp(final Entity entity, final float elapsedTime) {
        final float amountToMove = speed * elapsedTime;
        final Vector3f delta = new Vector3f(0.0f, amountToMove, 0.0f);
        entity.getTransform().translate(delta);
    }

    public void moveDown(final Entity entity, final float elapsedTime) {
        final float amountToMove = speed * elapsedTime;
        final Vector3f delta = new Vector3f(0.0f, -amountToMove, 0.0f);
        entity.getTransform().translate(delta);
    }

    public void moveRight(final Entity entity, final float elapsedTime) {
        final float amountToMove = speed * elapsedTime;
        final Vector3f delta = new Vector3f(amountToMove, 0.0f, 0.0f);
        entity.getTransform().translate(delta);
    }

    public void moveLeft(final Entity entity, final float elapsedTime) {
        final float amountToMove = speed * elapsedTime;
        final Vector3f delta = new Vector3f(-amountToMove, 0.0f, 0.0f);
        entity.getTransform().translate(delta);
    }
}
