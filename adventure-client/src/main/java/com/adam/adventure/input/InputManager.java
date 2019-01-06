package com.adam.adventure.input;

import com.adam.adventure.entity.component.console.UiConsoleComponent;
import com.adam.adventure.window.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;

public class InputManager implements GLFWKeyCallbackI {

    private final NiftyInputSystem lwjflInputSystem;
    private final boolean[] heldKeys;
    private final List<KeyPressListener> keyPressListeners;

    @Inject
    public InputManager(final Window window) throws Exception {
        this.lwjflInputSystem = new NiftyInputSystem(window.getWindowHandle());
        lwjflInputSystem.startup();
        this.heldKeys = new boolean[1024];
        this.keyPressListeners = new ArrayList<>();
        window.setKeyCallback(this);
        window.setCursorPositionCallback(new CursorMovementInputManager(lwjflInputSystem));
        window.setMouseButtonCallback(new MouseButtonInputManager(lwjflInputSystem));
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
            keyPressListeners.forEach(keyPressListener -> keyPressListener.onKeyPress(key));
        } else if (action == GLFW.GLFW_RELEASE) {
            heldKeys[key] = false;
        }

        // Forward to nifty gui
        lwjflInputSystem.keyCallback.invoke(window, key, scancode, action, mods);
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


    public NiftyInputSystem getLwjflInputSystem() {
        return lwjflInputSystem;
    }
}
