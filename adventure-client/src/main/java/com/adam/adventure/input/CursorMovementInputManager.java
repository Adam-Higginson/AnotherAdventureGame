package com.adam.adventure.input;

import org.lwjgl.glfw.GLFWCursorPosCallbackI;

public class CursorMovementInputManager implements GLFWCursorPosCallbackI {
    private final NiftyInputSystem lwjflInputSystem;

    public CursorMovementInputManager(final NiftyInputSystem lwjflInputSystem) {
        this.lwjflInputSystem = lwjflInputSystem;
    }

    @Override
    public void invoke(final long window, final double xpos, final double ypos) {
        lwjflInputSystem.cursorPosCallback.invoke(window, xpos, ypos);
    }
}
