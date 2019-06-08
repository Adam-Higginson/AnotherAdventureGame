package com.adam.adventure.scene;

import com.adam.adventure.event.Event;

public class NewSceneEvent extends Event {
    private final String sceneName;

    public NewSceneEvent(final String sceneName) {
        this.sceneName = sceneName;
    }

    public String getSceneName() {
        return sceneName;
    }
}
