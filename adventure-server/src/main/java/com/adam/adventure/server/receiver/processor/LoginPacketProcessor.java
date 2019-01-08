package com.adam.adventure.server.receiver.processor;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.lib.flatbuffer.schema.packet.LoginPacket;
import com.adam.adventure.lib.flatbuffer.schema.packet.Packet;
import com.adam.adventure.server.PlayerManager;
import com.adam.adventure.server.event.NewPlayerEvent;

import javax.inject.Inject;
import java.net.DatagramPacket;
import java.util.function.BiConsumer;

public class LoginPacketProcessor implements BiConsumer<DatagramPacket, Packet> {

    private final PlayerManager playerManager;
    private final EventBus eventBus;

    @Inject
    public LoginPacketProcessor(PlayerManager playerManager, EventBus eventBus) {
        this.playerManager = playerManager;
        this.eventBus = eventBus;
    }

    @Override
    public void accept(final DatagramPacket datagramPacket, Packet packet) {
        LoginPacket loginPacket = (LoginPacket) packet.packet(new LoginPacket());
        String username = loginPacket.username();

        PlayerManager.PlayerTableRow playerTableRow = playerManager.addPlayer(username, datagramPacket.getAddress(), datagramPacket.getPort());
        eventBus.publishEvent(new NewPlayerEvent(playerTableRow.getId()));
    }
}
