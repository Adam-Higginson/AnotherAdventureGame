package com.adam.adventure.server.player;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import com.adam.adventure.entity.component.tilemap.TilemapObserverComponent;
import com.adam.adventure.server.entity.component.NetworkAnimationComponent;
import com.adam.adventure.server.entity.component.NetworkIdComponent;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;

import javax.inject.Inject;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
public class PlayerSessionRegistry {
    private final EntityFactory entityFactory;
    private final PlayerSessionMap playerSessionMap;

    @Inject
    public PlayerSessionRegistry(final EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
        this.playerSessionMap = new PlayerSessionMap();
    }


    public PlayerSession addPlayer(final String username, final InetAddress address, final int port) {
        final PlayerSession existingPlayerSession = playerSessionMap.getByUsername(username);
        if (existingPlayerSession != null) {
            LOG.info("Player: {} already had active session, reusing this", username);
            updatePlayerAddress(existingPlayerSession.getId(), address, port);
            updatePlayerState(existingPlayerSession.getId(), PlayerSession.State.LOGGING_IN);
            return playerSessionMap.getById(existingPlayerSession.getId());
        }

        final UUID playerId = UUID.randomUUID();
        LOG.info("Adding player: {} with id: {}", username, playerId);

        final Entity playerEntity = entityFactory.create("Player-" + playerId);
        playerEntity.setTransform(buildRandomPositionMatrix());
        playerEntity.addComponent(new NetworkIdComponent(playerId));
        playerEntity.addComponent(new NetworkAnimationComponent());
        playerEntity.addComponent(new TilemapObserverComponent());

        final PlayerSession playerSession = PlayerSession.builder()
                .playerEntity(playerEntity)
                .address(address)
                .port(port)
                .id(playerId)
                .username(username)
                .lastReceivedUpdate(LocalDateTime.now())
                .state(PlayerSession.State.LOGGING_IN)
                .build();

        playerSessionMap.put(playerSession);
        return playerSession;
    }

    public void updatePlayerState(final UUID playerId, final PlayerSession.State newState) {
        final PlayerSession playerSession = playerSessionMap.removeById(playerId);
        final PlayerSession newPlayerSession = PlayerSession.builder(playerSession)
                .state(newState)
                .build();
        playerSessionMap.put(newPlayerSession);
    }

    public void updatePlayerAddress(final UUID playerId, final InetAddress address, final int port) {
        final PlayerSession playerSession = playerSessionMap.removeById(playerId);
        final PlayerSession newPlayerSession = PlayerSession.builder(playerSession)
                .address(address)
                .port(port)
                .build();
        playerSessionMap.put(newPlayerSession);
    }

    public Optional<PlayerSession> getById(final UUID playerId) {
        return Optional.ofNullable(playerSessionMap.getById(playerId));
    }


    public List<PlayerSession> getPlayerSessionsWithState(final PlayerSession.State playerSessionState) {
        return playerSessionMap.values().stream()
                .filter(session -> session.getState() == playerSessionState)
                .collect(Collectors.toList());
    }

    private Matrix4f buildRandomPositionMatrix() {
        return new Matrix4f().translate(ThreadLocalRandom.current().nextInt(-500, 500),
                ThreadLocalRandom.current().nextInt(-500, 500),
                0);
    }


    private class PlayerSessionMap {
        private final Map<UUID, PlayerSession> playerIdToSession = new ConcurrentHashMap<>();
        private final Map<String, PlayerSession> playerUsernameToSession = new ConcurrentHashMap<>();

        PlayerSession getById(final UUID id) {
            return playerIdToSession.get(id);
        }

        PlayerSession getByUsername(final String username) {
            return playerUsernameToSession.get(username);
        }

        PlayerSession removeById(final UUID id) {
            final PlayerSession existingSession = playerIdToSession.remove(id);
            if (existingSession != null) {
                playerUsernameToSession.remove(existingSession.getUsername());
            }

            return existingSession;
        }

        void put(final PlayerSession playerSession) {
            playerIdToSession.put(playerSession.getId(), playerSession);
            playerUsernameToSession.put(playerSession.getUsername(), playerSession);
        }

        Collection<PlayerSession> values() {
            return playerIdToSession.values();
        }
    }

}
