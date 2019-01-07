package com.adam.adventure.domain;

import org.joml.Matrix4f;

public class PlayerInfo {
    private int userId;
    private String username;
    private Matrix4f transform;

    public PlayerInfo(int userId, String username, Matrix4f transform) {
        this.userId = userId;
        this.username = username;
        this.transform = transform;
    }

    private PlayerInfo(Builder builder) {
        userId = builder.userId;
        username = builder.username;
        transform = builder.transform;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Matrix4f getTransform() {
        return transform;
    }


    public static final class Builder {
        private int userId;
        private String username;
        private Matrix4f transform;

        private Builder() {
        }

        public Builder withUserId(int val) {
            userId = val;
            return this;
        }

        public Builder withUsername(String val) {
            username = val;
            return this;
        }

        public Builder withTransform(Matrix4f val) {
            transform = val;
            return this;
        }

        public PlayerInfo build() {
            return new PlayerInfo(this);
        }
    }
}
