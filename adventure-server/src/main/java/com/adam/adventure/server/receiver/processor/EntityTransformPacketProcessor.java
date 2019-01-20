package com.adam.adventure.server.receiver.processor;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.lib.flatbuffer.schema.converter.PacketConverter;
import com.adam.adventure.lib.flatbuffer.schema.packet.EntityTransformPacket;
import com.adam.adventure.lib.flatbuffer.schema.packet.Packet;
import com.adam.adventure.server.tick.event.EntityTransformEvent;
import org.joml.Matrix4f;

import javax.inject.Inject;
import java.net.DatagramPacket;
import java.util.UUID;
import java.util.function.BiConsumer;

public class EntityTransformPacketProcessor implements BiConsumer<DatagramPacket, Packet> {

    private final EventBus eventBus;
    private final PacketConverter packetConverter;

    @Inject
    public EntityTransformPacketProcessor(final EventBus eventBus, final PacketConverter packetConverter) {
        this.eventBus = eventBus;
        this.packetConverter = packetConverter;
    }


    @Override
    public void accept(final DatagramPacket datagramPacket, final Packet packet) {
        final EntityTransformPacket entityTransformPacket = (EntityTransformPacket) packet.packet(new EntityTransformPacket());
        final UUID entityId = UUID.fromString(entityTransformPacket.entityId());
        final Matrix4f transform = packetConverter.fromPacketMatrix4f(entityTransformPacket.transform());

        eventBus.publishEvent(new EntityTransformEvent(entityId, transform));
    }
}
