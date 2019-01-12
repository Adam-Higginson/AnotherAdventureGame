package com.adam.adventure.server.state;

import com.adam.adventure.domain.PlayerInfo;
import com.adam.adventure.domain.SceneInfo;
import com.adam.adventure.domain.WorldState;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.lib.flatbuffer.schema.converter.PacketConverter;
import com.adam.adventure.server.player.PlayerSession;
import com.adam.adventure.server.player.PlayerSessionRegistry;
import com.adam.adventure.server.tick.OnNewServerTickEvent;
import com.adam.adventure.server.tick.OutputPacketQueue;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
                .withPlayers(new LinkedList<>())
                .withSceneInfo(new SceneInfo("Test Scene"))
                .build();

        eventBus.register(this);
    }

    @EventSubscribe
    public void onNewServerTickEvent(final OnNewServerTickEvent onNewServerTickEvent) {

        final Set<Integer> currentPlayerIdsInWorldState = worldState.getPlayers()
                .stream()
                .map(PlayerInfo::getId)
                .collect(Collectors.toSet());

        final List<PlayerSession> activePlayerSessions = playerSessionRegistry
                .getPlayerSessionsWithState(PlayerSession.State.ACTIVE);

        activePlayerSessions
                .stream()
                .filter(activePlayer -> !currentPlayerIdsInWorldState.contains(activePlayer.getId()))
                .forEach(activePlayerNotInWorld -> {
                    final PlayerInfo playerInfo = PlayerInfo.newBuilder()
                            .withId(activePlayerNotInWorld.getId())
                            .withUsername(activePlayerNotInWorld.getUsername())
                            .withTransform(activePlayerNotInWorld.getPlayerEntity().getTransform())
                            .build();
                    worldState.getPlayers().add(playerInfo);

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
