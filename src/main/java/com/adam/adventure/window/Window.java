package com.adam.adventure.window;

import com.adam.adventure.glfw.GlfwUtil;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private enum WindowState {INITIALISED, OPEN, CLOSED}

    private final boolean isVisible;
    private final boolean isResizable;

    // The window handle
    private final long glfwWindow;

    private WindowState windowState;


    public Window(final Builder builder) {
        this.isVisible = builder.isVisible;
        this.isResizable = builder.isResizable;
        this.glfwWindow = glfwCreateWindow(builder.width, builder.height, builder.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window");
        }

        windowState = WindowState.INITIALISED;
    }

    public void openWindow() {
        throwExceptionOnStateNotMatching(WindowState.INITIALISED);

        configureGlfw();
        setWindowPositionAsCenter();
        glfwMakeContextCurrent(glfwWindow);
        enableVsync();
        glfwShowWindow(glfwWindow);
        GL.createCapabilities();

        windowState = WindowState.OPEN;
    }


    public void clearWindow(final float red, final float green, final float blue, final float alpha) {
        throwExceptionOnStateNotMatching(WindowState.OPEN);
        glClearColor(red, green, blue, alpha);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(glfwWindow);
    }

    public void close() {
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);
        windowState = WindowState.CLOSED;
    }


    public void swapBuffers() {
        glfwSwapBuffers(glfwWindow);
    }

    //TODO use callback and determine latest set size
    public float getWidth() {
        return 800.f;
    }

    public float getHeight() {
        return 600.f;
    }


    private void setWindowPositionAsCenter() {
        try (MemoryStack stack = stackPush()) {
            final IntBuffer pWidth = stack.mallocInt(1);
            final IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(glfwWindow, pWidth, pHeight);
            final GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(
                    glfwWindow,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }
    }


    public void setKeyCallback(final GLFWKeyCallbackI keyCallback) {
        glfwSetKeyCallback(glfwWindow, keyCallback);
    }

    private void configureGlfw() {
        glfwWindowHint(GLFW_VISIBLE, GlfwUtil.booleanToGlfwInt(isVisible));
        glfwWindowHint(GLFW_RESIZABLE, GlfwUtil.booleanToGlfwInt(isResizable));
    }

    private void enableVsync() {
        glfwSwapInterval(1);
    }


    private void throwExceptionOnStateNotMatching(final WindowState expectedState) {
        if (windowState != expectedState) {
            throw new IllegalStateException("Expected window state to be: " + expectedState + " but was actually: " + windowState);
        }
    }

    public static class Builder {
        private final int width;
        private final int height;
        private String title = "";
        private boolean isVisible = false;
        private boolean isResizable = true;

        public Builder(final int width, final int height) {
            this.width = width;
            this.height = height;
        }

        public Builder withTitle(final String title) {
            this.title = title;
            return this;
        }

        public Builder withIsVisible(final boolean isVisible) {
            this.isVisible = isVisible;
            return this;
        }

        public Builder withIsResizable(final boolean isResizable) {
            this.isResizable = isResizable;
            return this;
        }

        public Window build() {
            return new Window(this);
        }
    }
}
