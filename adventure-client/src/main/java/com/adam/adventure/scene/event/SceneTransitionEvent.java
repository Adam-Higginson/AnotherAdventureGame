package com.adam.adventure.scene.event;

import com.adam.adventure.event.Event;
import com.adam.adventure.scene.Scene;

public class SceneTransitionEvent extends Event {
    private final Scene scene;

    public SceneTransitionEvent(final Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }
}
