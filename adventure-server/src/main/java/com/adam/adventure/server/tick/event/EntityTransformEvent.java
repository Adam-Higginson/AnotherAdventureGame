package com.adam.adventure.server.tick.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joml.Matrix4f;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class EntityTransformEvent extends ServerTickEvent {
    private final UUID entityId;
    private final Matrix4f transform;
}
