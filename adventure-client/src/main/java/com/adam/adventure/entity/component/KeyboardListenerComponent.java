package com.adam.adventure.entity.component;

import com.adam.adventure.entity.EntityComponent;
import com.adam.adventure.entity.InputLocked;
import com.adam.adventure.input.InputManager;

import javax.inject.Inject;

public class KeyboardListenerComponent extends EntityComponent {

    @Inject
    private InputManager inputManager;
    private boolean inputLocked;


    private final int glfwKey;
    private final Runnable actionToExecute;

    public KeyboardListenerComponent(final int glfwKey,
                                     final Runnable actionToExecute) {
        this.glfwKey = glfwKey;
        this.actionToExecute = actionToExecute;
    }

    @Override
    @InputLocked
    protected void update(final float deltaTime) {
        if (inputManager.isKeyPressed(glfwKey)) {
            actionToExecute.run();
        }
    }

}
