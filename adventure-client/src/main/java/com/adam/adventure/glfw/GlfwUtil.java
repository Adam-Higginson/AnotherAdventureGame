package com.adam.adventure.glfw;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;

public class GlfwUtil {

    private GlfwUtil() {
        //No instantiation
    }

    public static int booleanToGlfwInt(final boolean val) {
        return val ? GLFW_TRUE : GLFW_FALSE;
    }
}
