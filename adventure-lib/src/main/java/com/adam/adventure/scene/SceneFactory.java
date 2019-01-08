package com.adam.adventure.scene;

import com.adam.adventure.event.EventBus;

import javax.inject.Inject;

public class SceneFactory {
    private final EventBus eventBus;

    @Inject
    SceneFactory(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public Scene createScene(final String name) {
        return new Scene(eventBus, name);
    }
}
