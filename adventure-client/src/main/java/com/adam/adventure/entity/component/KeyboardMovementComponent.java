package com.adam.adventure.entity.component;

import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.input.InputManager;
import org.joml.Vector3f;

import javax.inject.Inject;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardMovementComponent extends EntityComponent {

    @Inject
    private InputManager inputManager;

    private boolean inputLocked;

    private final float speed;

    public KeyboardMovementComponent(final float speed) {
        this.speed = speed;
    }


    @Override
    protected void update(final float deltaTime) {
        if (inputLocked) {
            return;
        }

        ComponentEvent event = ComponentEvent.ENTITY_NO_MOVEMENT;

        if (inputManager.isKeyPressed(GLFW_KEY_W)) {
            moveUp(deltaTime);
            event = ComponentEvent.ENTITY_MOVE_NORTH;
        }
        if (inputManager.isKeyPressed(GLFW_KEY_S)) {
            moveDown(deltaTime);
            event = ComponentEvent.ENTITY_MOVE_SOUTH;
        }
        if (inputManager.isKeyPressed(GLFW_KEY_A)) {
            moveLeft(deltaTime);
            event = ComponentEvent.ENTITY_MOVE_WEST;
        }
        if (inputManager.isKeyPressed(GLFW_KEY_D)) {
            moveRight(deltaTime);
            event = ComponentEvent.ENTITY_MOVE_EAST;
        }

        //This only supports broadcasting of one component event to stop events cancelling themselves out.
        broadcastComponentEvent(event);
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

    private void moveRight(final float elapsedTime) {
        final float amountToMove = speed * elapsedTime;
        final Vector3f delta = new Vector3f(amountToMove, 0.0f, 0.0f);
        getTransformComponent().getTransform().translate(delta);
    }

    private void moveLeft(final float elapsedTime) {
        final float amountToMove = speed * elapsedTime;
        final Vector3f delta = new Vector3f(-amountToMove, 0.0f, 0.0f);
        getTransformComponent().getTransform().translate(delta);
    }
}
