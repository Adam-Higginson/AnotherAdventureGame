package com.adam.adventure.input;

import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

public class MouseButtonInputManager implements GLFWMouseButtonCallbackI {
    private final NiftyInputSystem lwjflInputSystem;

    public MouseButtonInputManager(final NiftyInputSystem lwjflInputSystem) {
        this.lwjflInputSystem = lwjflInputSystem;
    }

    @Override
    public void invoke(final long window, final int button, final int action, final int mods) {
        lwjflInputSystem.mouseButtonCallback.invoke(window, button, action, mods);
    }
}
