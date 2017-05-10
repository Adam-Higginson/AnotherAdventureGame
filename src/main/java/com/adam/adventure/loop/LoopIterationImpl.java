package com.adam.adventure.loop;

import com.adam.adventure.entity.PlayerEntity;
import com.adam.adventure.input.InputManager;
import com.adam.adventure.render.Renderer;
import com.adam.adventure.render.camera.Camera;

import static org.lwjgl.glfw.GLFW.*;

public class LoopIterationImpl implements LoopIteration {

    private final InputManager inputManager;
    private final Camera camera;
    private final PlayerEntity playerEntity;
    private final Renderer renderer;

    public LoopIterationImpl(final InputManager inputManager, final Camera camera, final PlayerEntity playerEntity, final Renderer renderer) {
        this.inputManager = inputManager;
        this.camera = camera;
        this.playerEntity = playerEntity;
        this.renderer = renderer;
    }

    @Override
    public void onNewIteration(final long elapsedTime) {
        processInput();
        update();
        renderer.render();
    }

    private void processInput() {
        glfwPollEvents();
    }

    private void update() {
        if (inputManager.isKeyPressed(GLFW_KEY_W)) {
            playerEntity.moveUp(1.f);
        }
        if (inputManager.isKeyPressed(GLFW_KEY_S)) {
            playerEntity.moveDown(1.f);
        }
        if (inputManager.isKeyPressed(GLFW_KEY_A)) {
            playerEntity.moveLeft(1.f);
        }
        if (inputManager.isKeyPressed(GLFW_KEY_D)) {
            playerEntity.moveRight(1.f);
        }


        if (inputManager.isKeyPressed(GLFW_KEY_UP)) {
            camera.moveUp(1.f);
        }
        if (inputManager.isKeyPressed(GLFW_KEY_DOWN)) {
            camera.moveDown(1.f);
        }
        if (inputManager.isKeyPressed(GLFW_KEY_LEFT)) {
            camera.moveLeft(1.f);
        }
        if (inputManager.isKeyPressed(GLFW_KEY_RIGHT)) {
            camera.moveRight(1.f);
        }

    }
}
