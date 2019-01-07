package com.adam.adventure.event;

public class SceneActivatedEvent extends Event {
    private final String sceneName;

    public SceneActivatedEvent(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getSceneName() {
        return sceneName;
    }
}

