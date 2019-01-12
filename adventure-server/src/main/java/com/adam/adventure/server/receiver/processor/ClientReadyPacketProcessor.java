package com.adam.adventure.server.receiver.processor;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.lib.flatbuffer.schema.packet.ClientReadyPacket;
import com.adam.adventure.lib.flatbuffer.schema.packet.Packet;
import com.adam.adventure.server.event.ClientReadyEvent;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.DatagramPacket;
import java.util.function.BiConsumer;

@Slf4j
public class ClientReadyPacketProcessor implements BiConsumer<DatagramPacket, Packet> {

    private final EventBus eventBus;

    @Inject
    public ClientReadyPacketProcessor(final EventBus eventBus) {
        this.eventBus = eventBus;
    }


    @Override
    public void accept(final DatagramPacket datagramPacket, final Packet packet) {
        final ClientReadyPacket clientReadyPacket = (ClientReadyPacket) packet.packet(new ClientReadyPacket());
        final int playerId = clientReadyPacket.player().userId();
        LOG.info("Received client ready packet from player id: {}", playerId);

        eventBus.publishEvent(new ClientReadyEvent(playerId));
    }

}
