package com.adam.adventure.window;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.glfw.GlfwUtil;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {


    private enum WindowState {INITIALISED, OPEN, CLOSED;}


    // The window handle
    private final long glfwWindow;

    private final EventBus eventBus;
    private final boolean isVisible;
    private final boolean isResizable;
    private int width;
    private int height;
    private WindowState windowState;

    public Window(final Builder builder) {
        this.eventBus = builder.eventBus;
        this.isVisible = builder.isVisible;
        this.isResizable = builder.isResizable;
        this.width = builder.width;
        this.height = builder.height;
        this.glfwWindow = glfwCreateWindow(width, height, builder.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window");
        }

        glfwSetWindowSizeCallback(glfwWindow, (window, width, height) -> resize(width, height));
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void setWindowPositionAsCenter() {
        try (final MemoryStack stack = stackPush()) {
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

    public void setCursorPositionCallback(final GLFWCursorPosCallbackI cursorPositionCallback) {
        glfwSetCursorPosCallback(glfwWindow, cursorPositionCallback);
    }

    public void setMouseButtonCallback(final GLFWMouseButtonCallbackI mouseButtonCallback) {
        glfwSetMouseButtonCallback(glfwWindow, mouseButtonCallback);
    }

    public void setScrollCallback(final GLFWScrollCallbackI scrollCallback) {
        glfwSetScrollCallback(glfwWindow, scrollCallback);
    }

    public long getWindowHandle() {
        return glfwWindow;
    }

    private void configureGlfw() {
        glfwWindowHint(GLFW_VISIBLE, GlfwUtil.booleanToGlfwInt(isVisible));
        glfwWindowHint(GLFW_RESIZABLE, GlfwUtil.booleanToGlfwInt(isResizable));
    }

    private void enableVsync() {
        glfwSwapInterval(1);
    }

    private void resize(final int width, final int height) {
        this.width = width;
        this.height = height;
        glViewport(0, 0, width, height);
        eventBus.publishEvent(new WindowResizeEvent(width, height));
    }


    private void throwExceptionOnStateNotMatching(final WindowState expectedState) {
        if (windowState != expectedState) {
            throw new IllegalStateException("Expected window state to be: " + expectedState + " but was actually: " + windowState);
        }
    }

    public static class Builder {
        private final int width;
        private final int height;
        private EventBus eventBus;
        private String title = "";
        private boolean isVisible = false;
        private boolean isResizable = true;

        public Builder(final int width, final int height, final EventBus eventBus) {
            this.width = width;
            this.height = height;
            this.eventBus = eventBus;
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
