package com.adam.adventure.domain;

import java.util.List;

public class SceneInfo {
    private final String sceneName;
    private final List<EntityInfo> entities;

    private SceneInfo(final Builder builder) {
        sceneName = builder.sceneName;
        entities = builder.entities;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(final SceneInfo copy) {
        final Builder builder = new Builder();
        builder.sceneName = copy.getSceneName();
        builder.entities = copy.getEntities();
        return builder;
    }

    public String getSceneName() {
        return sceneName;
    }

    public List<EntityInfo> getEntities() {
        return entities;
    }

    public static final class Builder {
        private String sceneName;
        private List<EntityInfo> entities;

        private Builder() {
        }

        public Builder sceneName(final String sceneName) {
            this.sceneName = sceneName;
            return this;
        }

        public Builder entities(final List<EntityInfo> entities) {
            this.entities = entities;
            return this;
        }

        public SceneInfo build() {
            return new SceneInfo(this);
        }
    }
}
