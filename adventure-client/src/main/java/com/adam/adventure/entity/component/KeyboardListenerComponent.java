package com.adam.adventure.entity.component;

import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.input.InputManager;

public class KeyboardListenerComponent extends EntityComponent {


    private final InputManager inputManager;
    private final int glfwKey;
    private final Runnable actionToExecute;

    public KeyboardListenerComponent(final InputManager inputManager,
                                     final int glfwKey,
                                     final Runnable actionToExecute) {
        this.inputManager = inputManager;
        this.glfwKey = glfwKey;
        this.actionToExecute = actionToExecute;
    }

    @Override
    protected void update(final float deltaTime) {
        if (inputManager.isKeyPressed(glfwKey)) {
            actionToExecute.run();
        }
    }

    @Override
    protected void onComponentEvent(final ComponentEvent componentEvent) {
        // Nothing needs doing
    }
}
