package com.adam.adventure.loop;

import com.adam.adventure.input.InputManager;
import com.adam.adventure.render.camera.Camera;
import com.adam.adventure.render.camera.Renderer;

import static org.lwjgl.glfw.GLFW.*;

public class LoopIterationImpl implements LoopIteration {

    private final InputManager inputManager;
    private final Camera camera;
    private final Renderer renderer;

    public LoopIterationImpl(final InputManager inputManager, final Camera camera, final Renderer renderer) {
        this.inputManager = inputManager;
        this.camera = camera;
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
            camera.moveUp(1.f);
        }
        if (inputManager.isKeyPressed(GLFW_KEY_S)) {
            camera.moveDown(1.f);
        }
        if (inputManager.isKeyPressed(GLFW_KEY_A)) {
            camera.moveLeft(1.f);
        }
        if (inputManager.isKeyPressed(GLFW_KEY_D)) {
            camera.moveRight(1.f);
        }
    }
}
