package com.adam.adventure.server.state;

import com.adam.adventure.domain.EntityInfo;
import com.adam.adventure.domain.SceneInfo;
import com.adam.adventure.domain.WorldState;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.lib.flatbuffer.schema.converter.PacketConverter;
import com.adam.adventure.server.player.PlayerSession;
import com.adam.adventure.server.player.PlayerSessionRegistry;
import com.adam.adventure.server.tick.OnNewServerTickEvent;
import com.adam.adventure.server.tick.OutputPacketQueue;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class WorldStateManager {
    private final WorldState worldState;
    private final PlayerSessionRegistry playerSessionRegistry;
    private final PacketConverter packetConverter;

    @Inject
    public WorldStateManager(final EventBus eventBus,
                             final PlayerSessionRegistry playerSessionRegistry,
                             final PacketConverter packetConverter) {
        this.playerSessionRegistry = playerSessionRegistry;
        this.packetConverter = packetConverter;
        this.worldState = WorldState.newBuilder()
                .withSceneInfo(SceneInfo.newBuilder()
                        .sceneName("Test Scene")
                        .entities(new ArrayList<>())
                        .build())
                .build();

        eventBus.register(this);
    }

    @EventSubscribe
    public void onNewServerTickEvent(final OnNewServerTickEvent onNewServerTickEvent) {

        final Set<UUID> currentPlayerIdsInWorldState = worldState.getSceneInfo()
                .getEntities()
                .stream()
                .filter(entity -> entity.getType() == EntityInfo.EntityType.PLAYER)
                .map(EntityInfo::getId)
                .collect(Collectors.toSet());

        final List<PlayerSession> activePlayerSessions = playerSessionRegistry
                .getPlayerSessionsWithState(PlayerSession.State.ACTIVE);

        activePlayerSessions
                .stream()
                .filter(activePlayer -> !currentPlayerIdsInWorldState.contains(activePlayer.getId()))
                .forEach(activePlayerNotInWorld -> {
                    final EntityInfo playerInfo = EntityInfo.newBuilder()
                            .id(activePlayerNotInWorld.getId())
                            .attributes(ImmutableMap.of("username", activePlayerNotInWorld.getUsername()))
                            .transform(activePlayerNotInWorld.getPlayerEntity().getTransform())
                            .type(EntityInfo.EntityType.PLAYER)
                            .build();
                    worldState.getSceneInfo().getEntities().add(playerInfo);

                    //TODO If player has logged out/timed out you would remove them here
                });

        final OutputPacketQueue outputPacketQueue = onNewServerTickEvent
                .getOutputPacketQueue();
        final byte[] worldStatePacket = packetConverter.buildWorldStatePacket(worldState);
        activePlayerSessions.forEach(activePlayerSession -> {
            outputPacketQueue.addOutputPacketSupplier(() -> new DatagramPacket(worldStatePacket,
                    worldStatePacket.length,
                    activePlayerSession.getAddress(),
                    activePlayerSession.getPort()));
        });
    }
}
