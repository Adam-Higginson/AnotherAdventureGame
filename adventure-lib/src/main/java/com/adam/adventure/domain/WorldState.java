package com.adam.adventure.domain;

import java.util.List;
import java.util.stream.Collectors;

public class WorldState {
    private final SceneInfo sceneInfo;

    private WorldState(final Builder builder) {
        sceneInfo = builder.sceneInfo;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public SceneInfo getSceneInfo() {
        return sceneInfo;
    }

    public List<EntityInfo> getPlayerEntities() {
        return sceneInfo.getEntities()
                .stream()
                .filter(entity -> entity.getType() == EntityInfo.EntityType.PLAYER)
                .collect(Collectors.toList());
    }

    public static final class Builder {
        private SceneInfo sceneInfo;

        private Builder() {
        }

        public Builder withSceneInfo(final SceneInfo val) {
            sceneInfo = val;
            return this;
        }

        public WorldState build() {
            return new WorldState(this);
        }
    }
}
