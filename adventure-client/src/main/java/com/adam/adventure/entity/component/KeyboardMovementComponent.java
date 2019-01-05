package com.adam.adventure.entity.component;

import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.input.InputManager;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardMovementComponent extends EntityComponent {

    private final float speed;
    private final InputManager inputManager;

    public KeyboardMovementComponent(final ComponentContainer componentContainer,
                                     final float speed,
                                     final InputManager inputManager) {
        super(componentContainer);
        this.speed = speed;
        this.inputManager = inputManager;
    }


    @Override
    protected void update(final float deltaTime) {
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

    @Override
    public void onComponentEvent(final ComponentEvent componentEvent) {
        //Nothing doing
    }


    private void moveUp(final float elapsedTime) {
        final float amountToMove = speed * elapsedTime;
        final Vector3f delta = new Vector3f(0.0f, amountToMove, 0.0f);
        getTransformComponent().getTransform().translate(delta);
    }

    public void moveDown(final float elapsedTime) {
        final float amountToMove = speed * elapsedTime;
        final Vector3f delta = new Vector3f(0.0f, -amountToMove, 0.0f);
        getTransformComponent().getTransform().translate(delta);
    }

    public void moveRight(final float elapsedTime) {
        final float amountToMove = speed * elapsedTime;
        final Vector3f delta = new Vector3f(amountToMove, 0.0f, 0.0f);
        getTransformComponent().getTransform().translate(delta);
    }

    public void moveLeft(final float elapsedTime) {
        final float amountToMove = speed * elapsedTime;
        final Vector3f delta = new Vector3f(-amountToMove, 0.0f, 0.0f);
        getTransformComponent().getTransform().translate(delta);
    }
}
