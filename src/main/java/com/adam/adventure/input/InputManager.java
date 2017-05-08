package com.adam.adventure.input;

import com.adam.adventure.window.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;

public class InputManager implements GLFWKeyCallbackI {

    private final boolean[] heldKeys;

    public InputManager(final Window window) {
        this.heldKeys = new boolean[1024];
        window.setKeyCallback(this);
    }

    @Override
    public void invoke(final long window, final int key, final int scancode, final int action, final int mods) {
        if (action == GLFW.GLFW_PRESS) {
            heldKeys[key] = true;
        } else if (action == GLFW.GLFW_RELEASE) {
            heldKeys[key] = false;
        }
    }

    public boolean isKeyPressed(final int key) {
        return heldKeys[key];
    }
}
