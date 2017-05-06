package com.adam.adventure.loop;

import com.adam.adventure.render.Renderer;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;

public class LoopIterationImpl implements LoopIteration {

    private final Renderer renderer;

    public LoopIterationImpl(final Renderer renderer) {
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

    }
}
