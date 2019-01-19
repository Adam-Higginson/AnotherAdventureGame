package com.adam.adventure.server.player;

import com.adam.adventure.domain.EntityInfo;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.lib.flatbuffer.schema.converter.PacketConverter;
import com.adam.adventure.server.tick.OnNewServerTickEvent;
import com.adam.adventure.server.tick.OutputPacketQueue;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.DatagramPacket;

@Slf4j
public class PlayerLoginCompleter {

    private final PlayerSessionRegistry playerSessionRegistry;
    private final PacketConverter packetConverter;

    @Inject
    public PlayerLoginCompleter(final EventBus eventBus,
                                final PlayerSessionRegistry playerSessionRegistry,
                                final PacketConverter packetConverter) {
        this.playerSessionRegistry = playerSessionRegistry;
        this.packetConverter = packetConverter;
        eventBus.register(this);
    }

    @EventSubscribe
    @SuppressWarnings("unused")
    public void onNewServerTick(final OnNewServerTickEvent onNewServerTickEvent) {
        completeLoginOfPlayers(onNewServerTickEvent.getOutputPacketQueue());
    }

    private void completeLoginOfPlayers(final OutputPacketQueue outputPacketQueue) {
        playerSessionRegistry
                .getPlayerSessionsWithState(PlayerSession.State.LOGGING_IN)
                .forEach(playerSession -> {
                    try {
                        returnLoginSuccessfulPacket(playerSession, outputPacketQueue);
                    } catch (final Exception e) {
                        LOG.error("Exception when attempting to return login successful for player: {}", playerSession.getUsername(), e);
                    }
                });
    }

    private void returnLoginSuccessfulPacket(final PlayerSession playerSession, final OutputPacketQueue outputPacketQueue) {
        outputPacketQueue.addOutputPacketSupplier(() -> {
            LOG.info("Sending login successful packet to player: {}", playerSession.getUsername());

            final EntityInfo playerInfo = EntityInfo.newBuilder()
                    .id(playerSession.getId())
                    .attributes(ImmutableMap.of("username", playerSession.getUsername()))
                    .transform(playerSession.getPlayerEntity().getTransform())
                    .type(EntityInfo.EntityType.PLAYER)
                    .build();
            final byte[] loginSuccessfulPacket = packetConverter.buildLoginSuccessfulPacket(playerInfo);
            return new DatagramPacket(loginSuccessfulPacket, loginSuccessfulPacket.length, playerSession.getAddress(), playerSession.getPort());
        });
    }
}
