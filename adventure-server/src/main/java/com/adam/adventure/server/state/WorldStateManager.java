package com.adam.adventure.server.state;

import com.adam.adventure.domain.EntityInfo;
import com.adam.adventure.domain.SceneInfo;
import com.adam.adventure.domain.WorldState;
import com.adam.adventure.entity.AnimationName;
import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.NewLoopIterationEvent;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.lib.flatbuffer.schema.converter.PacketConverter;
import com.adam.adventure.scene.RequestNewSceneEvent;
import com.adam.adventure.scene.Scene;
import com.adam.adventure.scene.SceneManager;
import com.adam.adventure.server.entity.component.NetworkAnimationComponent;
import com.adam.adventure.server.entity.component.NetworkIdComponent;
import com.adam.adventure.server.player.PlayerSession;
import com.adam.adventure.server.player.PlayerSessionRegistry;
import com.adam.adventure.server.tick.OnNewServerTickEvent;
import com.adam.adventure.server.tick.OutputPacketQueue;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;

import javax.inject.Inject;
import java.net.DatagramPacket;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class WorldStateManager {
    private final EventBus eventBus;
    private final PlayerSessionRegistry playerSessionRegistry;
    private final SceneManager sceneManager;
    private final PacketConverter packetConverter;

    @Inject
    public WorldStateManager(final EventBus eventBus,
                             final PlayerSessionRegistry playerSessionRegistry,
                             final SceneManager sceneManager,
                             final PacketConverter packetConverter) {
        this.eventBus = eventBus;
        this.playerSessionRegistry = playerSessionRegistry;
        this.sceneManager = sceneManager;
        this.packetConverter = packetConverter;

        eventBus.register(this);
    }

    @EventSubscribe
    @SuppressWarnings("unused")
    public void onNewServerTickEvent(final OnNewServerTickEvent onNewServerTickEvent) {
        //For now if no current scene, just set to a hardcoded test scene
        if (sceneManager.getCurrentScene().isEmpty()) {
            eventBus.publishEvent(new RequestNewSceneEvent("Test Scene"));
        }
        //Give all entities a chance to update
        eventBus.publishEvent(new NewLoopIterationEvent(onNewServerTickEvent.getDeltaTime()));
        final List<PlayerSession> activePlayerSessions = playerSessionRegistry.getPlayerSessionsWithState(PlayerSession.State.ACTIVE);

        final WorldState worldState = buildWorldState(activePlayerSessions);
        final OutputPacketQueue outputPacketQueue = onNewServerTickEvent
                .getOutputPacketQueue();
        activePlayerSessions.forEach(activePlayerSession -> returnWorldStatePacketToPlayer(activePlayerSession, worldState, outputPacketQueue));
    }

    private WorldState buildWorldState(final List<PlayerSession> activePlayerSessions) {
        final Scene currentScene = sceneManager
                .getCurrentScene()
                .orElseThrow(() -> new IllegalStateException("No current scene found!"));

        final Map<UUID, String> playerEntityIdToUsername = activePlayerSessions
                .stream()
                .collect(Collectors.toMap(PlayerSession::getId, PlayerSession::getUsername));

        final List<EntityInfo> entityInfoList = currentScene.getEntities()
                .stream()
                .map(entity -> buildEntityInfoFromEntity(entity, playerEntityIdToUsername))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        return WorldState.newBuilder()
                .withSceneInfo(SceneInfo.newBuilder()
                        .sceneName(currentScene.getName())
                        .entities(entityInfoList)
                        .build())
                .build();
    }


    private Optional<EntityInfo> buildEntityInfoFromEntity(final Entity entity, final Map<UUID, String> playerIdToUsername) {
        if (entity.getComponent(NetworkIdComponent.class).isEmpty()) {
            return Optional.empty();
        }

        final UUID entityId = entity.getComponent(NetworkIdComponent.class).get().getEntityId();
        final Matrix4f transform = entity.getTransform();
        final EntityInfo.EntityType entityType = playerIdToUsername.get(entityId) == null ?
                EntityInfo.EntityType.STANDARD :
                EntityInfo.EntityType.PLAYER;

        final Map<String, String> attributes = new HashMap<>();
        if (entityType == EntityInfo.EntityType.PLAYER) {
            attributes.put("username", playerIdToUsername.get(entityId));
        }

        final String animationName = entity.getComponent(NetworkAnimationComponent.class)
                .map(NetworkAnimationComponent::getAnimationName)
                .orElse(AnimationName.NO_MOVEMENT);

        return Optional.of(EntityInfo.newBuilder()
                .id(entityId)
                .name(entity.getName())
                .animationName(animationName)
                .transform(transform)
                .type(entityType)
                .attributes(attributes)
                .build());
    }

    private void returnWorldStatePacketToPlayer(final PlayerSession playerSession,
                                                final WorldState worldState,
                                                final OutputPacketQueue outputPacketQueue) {
        outputPacketQueue.addOutputPacketSupplier((packetIndex, timestamp) -> {
            final byte[] worldStatePacket = packetConverter.buildWorldStatePacket(worldState, packetIndex, timestamp);
            return new DatagramPacket(worldStatePacket,
                    worldStatePacket.length,
                    playerSession.getAddress(),
                    playerSession.getPort());

        });
    }
}
