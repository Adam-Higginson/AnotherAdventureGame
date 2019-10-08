package com.adam.adventure.domain;

import org.joml.Matrix4f;

import java.util.Map;
import java.util.UUID;

public class EntityInfo {
    public enum EntityType {STANDARD, PLAYER, TILEMAP}

    private final UUID id;
    private final String name;
    private final String animationName;
    private Matrix4f transform;
    private final Map<String, String> attributes;
    private final EntityType type;

    private EntityInfo(final Builder builder) {
        id = builder.id;
        name = builder.name;
        animationName = builder.animationName;
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
        builder.name = copy.getName();
        builder.animationName = copy.getAnimationName();
        builder.transform = copy.getTransform();
        builder.attributes = copy.getAttributes();
        builder.type = copy.getType();
        return builder;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAnimationName() {
        return animationName;
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
                "tileSetId=" + id +
                ", name='" + name + '\'' +
                ", animationName='" + animationName + '\'' +
                ", transform=" + transform +
                ", attributes=" + attributes +
                ", type=" + type +
                '}';
    }

    public static final class Builder {
        private UUID id;
        private String name;
        private String animationName;
        private Matrix4f transform;
        private Map<String, String> attributes;
        private EntityType type;

        private Builder() {
        }

        public Builder id(final UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder animationName(final String animationName) {
            this.animationName = animationName;
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
