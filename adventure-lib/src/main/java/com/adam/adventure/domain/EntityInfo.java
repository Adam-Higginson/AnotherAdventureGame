package com.adam.adventure.domain;

import org.joml.Matrix4f;

import java.util.Map;
import java.util.UUID;

public class EntityInfo {
    public enum EntityType {STANDARD, PLAYER}

    private final UUID id;
    private Matrix4f transform;
    private final Map<String, String> attributes;
    private final EntityType type;

    private EntityInfo(final Builder builder) {
        id = builder.id;
        transform = builder.transform;
        attributes = builder.attributes;
        type = builder.type;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(final EntityInfo copy) {
        final Builder builder = new Builder();
        builder.id = copy.getId();
        builder.transform = copy.getTransform();
        builder.attributes = copy.getAttributes();
        builder.type = copy.getType();
        return builder;
    }

    public UUID getId() {
        return id;
    }

    public Matrix4f getTransform() {
        return transform;
    }

    public void setTransform(final Matrix4f transform) {
        this.transform = transform;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public EntityType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "EntityInfo{" +
                "id=" + id +
                ", transform=" + transform +
                ", attributes=" + attributes +
                ", type=" + type +
                '}';
    }

    public static final class Builder {
        private UUID id;
        private Matrix4f transform;
        private Map<String, String> attributes;
        private EntityType type;

        private Builder() {
        }

        public Builder id(final UUID id) {
            this.id = id;
            return this;
        }

        public Builder transform(final Matrix4f transform) {
            this.transform = transform;
            return this;
        }

        public Builder attributes(final Map<String, String> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder type(final EntityType type) {
            this.type = type;
            return this;
        }

        public EntityInfo build() {
            return new EntityInfo(this);
        }
    }
}
