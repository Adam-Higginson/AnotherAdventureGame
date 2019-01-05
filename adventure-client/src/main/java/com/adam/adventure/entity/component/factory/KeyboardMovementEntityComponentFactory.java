package com.adam.adventure.entity.component.factory;

import com.adam.adventure.entity.component.ComponentContainer;
import com.adam.adventure.entity.component.EntityComponent;
import com.adam.adventure.entity.component.KeyboardMovementComponent;
import com.adam.adventure.input.InputManager;

public class KeyboardMovementEntityComponentFactory implements EntityComponentFactory {
    private final float speed;
    private final InputManager inputManager;

    public KeyboardMovementEntityComponentFactory(final float speed, final InputManager inputManager) {
        this.speed = speed;
        this.inputManager = inputManager;
    }

    @Override
    public void registerNewInstanceWithContainer(final ComponentContainer componentContainer) {
        final EntityComponent component = new KeyboardMovementComponent(componentContainer, speed, inputManager);
        componentContainer.addComponent(component);
    }
}
