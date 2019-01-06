package com.adam.adventure.entity.component;

import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.entity.component.event.ComponentEvent;
import com.adam.adventure.input.InputManager;

import javax.inject.Inject;

public class KeyboardListenerComponent extends EntityComponent {

    @Inject
    private InputManager inputManager;

    private final int glfwKey;
    private final Runnable actionToExecute;

    public KeyboardListenerComponent(final int glfwKey,
                                     final Runnable actionToExecute) {
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
