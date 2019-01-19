package com.adam.adventure.server.receiver.processor;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.lib.flatbuffer.schema.packet.LoginPacket;
import com.adam.adventure.lib.flatbuffer.schema.packet.Packet;
import com.adam.adventure.server.event.NewPlayerEvent;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.function.BiConsumer;

@Slf4j
public class LoginPacketProcessor implements BiConsumer<DatagramPacket, Packet> {

    private final EventBus eventBus;

    @Inject
    public LoginPacketProcessor(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void accept(final DatagramPacket datagramPacket, final Packet packet) {
        final LoginPacket loginPacket = (LoginPacket) packet.packet(new LoginPacket());
        final String username = loginPacket.playerUsername();
        final InetAddress address = datagramPacket.getAddress();
        final int port = datagramPacket.getPort();

        LOG.info("Received new login request from username: {} with address: {}, port: {}", username, address, port);

        eventBus.publishEvent(new NewPlayerEvent(username, address, port));
    }
}
