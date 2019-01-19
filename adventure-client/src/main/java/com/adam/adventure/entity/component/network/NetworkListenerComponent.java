package com.adam.adventure.entity.component.network;

import com.adam.adventure.entity.EntityComponent;

public class NetworkListenerComponent extends EntityComponent {

    @Override
    protected void update(final float deltaTime) {
        getTransformComponent();
    }
}
