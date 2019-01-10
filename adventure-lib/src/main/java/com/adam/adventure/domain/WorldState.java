package com.adam.adventure.domain;

import java.util.ArrayList;
import java.util.List;

public class WorldState
{
    private SceneInfo sceneInfo;
    private List<PlayerInfo> players;

    public WorldState() {
        this.players = new ArrayList<>();
    }

    private WorldState(Builder builder) {
        sceneInfo = builder.sceneInfo;
        players = builder.players;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public SceneInfo getSceneInfo() {
        return sceneInfo;
    }


    public List<PlayerInfo> getPlayers() {
        return players;
    }


    public static final class Builder {
        private SceneInfo sceneInfo;
        private List<PlayerInfo> players;

        private Builder() {
        }

        public Builder withSceneInfo(SceneInfo val) {
            sceneInfo = val;
            return this;
        }

        public Builder withPlayers(List<PlayerInfo> val) {
            players = val;
            return this;
        }

        public WorldState build() {
            return new WorldState(this);
        }
    }
}
