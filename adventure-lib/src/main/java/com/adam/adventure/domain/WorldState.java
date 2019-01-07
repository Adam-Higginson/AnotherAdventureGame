package com.adam.adventure.domain;

import java.util.List;

public class WorldState
{
    private SceneInfo sceneInfo;
    private PlayerInfo currentPlayer;
    private List<PlayerInfo> players;

    private WorldState(Builder builder) {
        sceneInfo = builder.sceneInfo;
        currentPlayer = builder.currentPlayer;
        players = builder.players;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public SceneInfo getSceneInfo() {
        return sceneInfo;
    }

    public PlayerInfo getCurrentPlayer() {
        return currentPlayer;
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }


    public static final class Builder {
        private SceneInfo sceneInfo;
        private PlayerInfo currentPlayer;
        private List<PlayerInfo> players;

        private Builder() {
        }

        public Builder withSceneInfo(SceneInfo val) {
            sceneInfo = val;
            return this;
        }

        public Builder withCurrentPlayer(PlayerInfo val) {
            currentPlayer = val;
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
