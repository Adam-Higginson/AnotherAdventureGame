package com.adam.adventure.domain.message;

import com.adam.adventure.lib.flatbuffer.schema.converter.PacketConverter;
import com.adam.adventure.lib.flatbuffer.schema.packet.EntityTransformPacket;
import com.google.flatbuffers.FlatBufferBuilder;
import org.joml.Matrix4f;

import java.util.UUID;

public class EntityTransformPacketableMessage implements PacketableMessage<EntityTransformPacket> {

    private UUID entityId;
    private Matrix4f transform;

    public EntityTransformPacketableMessage(final UUID entityId, final Matrix4f transform) {
        this.entityId = entityId;
        this.transform = transform;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(final UUID entityId) {
        this.entityId = entityId;
    }

    public Matrix4f getTransform() {
        return transform;
    }

    public void setTransform(final Matrix4f transform) {
        this.transform = transform;
    }

    @Override
    public int serialise(final FlatBufferBuilder builder, final PacketConverter packetConverter, final long packetId) {
        return packetConverter.buildEntityTransformPacket(builder, entityId, transform, packetId);
    }

    @Override
    public void deserialise(final EntityTransformPacket packet, final PacketConverter packetConverter, final long packetId) {
        setEntityId(UUID.fromString(packet.entityId()));
        setTransform(packetConverter.fromPacketMatrix4f(packet.transform()));
    }
}
