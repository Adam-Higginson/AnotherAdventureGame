package com.adam.adventure.input;

import com.adam.adventure.entity.component.console.UiConsoleComponent;
import com.adam.adventure.window.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;

public class InputManager implements GLFWKeyCallbackI {
    private static final Logger LOG = LoggerFactory.getLogger(InputManager.class);

    private final NiftyInputSystem lwjglInputSystem;
    private final boolean[] heldKeys;
    private final List<KeyPressListener> keyPressListeners;

    @Inject
    public InputManager(final Window window) throws Exception {
        this.lwjglInputSystem = new NiftyInputSystem(window.getWindowHandle());
        lwjglInputSystem.startup();
        this.heldKeys = new boolean[1024];
        this.keyPressListeners = new ArrayList<>();
        window.setKeyCallback(this);
        window.setCursorPositionCallback(new CursorMovementInputManager(lwjglInputSystem));
        window.setMouseButtonCallback(new MouseButtonInputManager(lwjglInputSystem));
    }

    public void processInput() {
        glfwPollEvents();
    }

    /**
     * Invoked when a keyboard key is pressed
     */
    @Override
    public void invoke(final long window, final int key, final int scancode, final int action, final int mods) {
        try {
            if (key == GLFW.GLFW_KEY_UNKNOWN) {
                return;
            }

            else if (action == GLFW.GLFW_PRESS) {
                heldKeys[key] = true;
                keyPressListeners.forEach(keyPressListener -> keyPressListener.onKeyPress(key));
            } else if (action == GLFW.GLFW_RELEASE) {
                heldKeys[key] = false;
            }

            // Forward to nifty gui
            lwjglInputSystem.keyCallback.invoke(window, key, scancode, action, mods);
        } catch (Exception e) {
            LOG.error("Exception in handling input in callback!", e);
        }
    }


    public void addKeyPressListener(final KeyPressListener keyPressListener) {
        keyPressListeners.add(keyPressListener);
    }


    public void removeKeyPressListener(final UiConsoleComponent component) {
        keyPressListeners.remove(component);
    }

    public boolean isKeyPressed(final int key) {
        return heldKeys[key];
    }


    public NiftyInputSystem getLwjglInputSystem() {
        return lwjglInputSystem;
    }
}
