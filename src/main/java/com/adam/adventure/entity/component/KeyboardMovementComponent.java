package com.adam.adventure.entity.component;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.component.event.ComponentEvent;
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
    public void update(final Entity target, final float elapsedTime, final ComponentContainer componentContainer) {
        ComponentEvent event = ComponentEvent.NO_MOVEMENT;

        if (inputManager.isKeyPressed(GLFW_KEY_W)) {
            moveUp(target, elapsedTime);
            event = ComponentEvent.ENTITY_MOVE_NORTH;
        }
        if (inputManager.isKeyPressed(GLFW_KEY_S)) {
            moveDown(target, elapsedTime);
            event = ComponentEvent.ENTITY_MOVE_SOUTH;
        }
        if (inputManager.isKeyPressed(GLFW_KEY_A)) {
            moveLeft(target, elapsedTime);
            event = ComponentEvent.ENTITY_MOVE_WEST;
        }
        if (inputManager.isKeyPressed(GLFW_KEY_D)) {
            moveRight(target, elapsedTime);
            event = ComponentEvent.ENTITY_MOVE_EAST;
        }

        //This only supports broadcasting of one component event to stop events cancelling themselves out.
        componentContainer.broadcastComponentEvent(event);
    }

    @Override
    public void onComponentEvent(final ComponentEvent componentEvent) {
        //Nothing doing
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
