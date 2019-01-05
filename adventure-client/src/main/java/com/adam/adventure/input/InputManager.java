package com.adam.adventure.input;

import com.adam.adventure.window.Window;
import de.lessvoid.nifty.renderer.lwjgl3.input.Lwjgl3InputSystem;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;

public class InputManager implements GLFWKeyCallbackI {

    private final Lwjgl3InputSystem lwjflInputSystem;
    private final boolean[] heldKeys;

    public InputManager(final Window window) throws Exception {
        this.lwjflInputSystem = new Lwjgl3InputSystem(window.getWindowHandle());
        lwjflInputSystem.startup();
        this.heldKeys = new boolean[1024];
        window.setKeyCallback(this);
    }

    public void processInput() {
        glfwPollEvents();
    }

    /**
     * Invoked when a keyboard key is pressed
     */
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

    public Lwjgl3InputSystem getLwjflInputSystem() {
        return lwjflInputSystem;
    }
}
