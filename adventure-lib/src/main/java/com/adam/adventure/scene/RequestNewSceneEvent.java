package com.adam.adventure.scene;

import com.adam.adventure.event.Event;

public class RequestNewSceneEvent extends Event {
    private final String sceneName;

    public RequestNewSceneEvent(final String sceneName) {
        this.sceneName = sceneName;
    }

    public String getSceneName() {
        return sceneName;
    }
}
