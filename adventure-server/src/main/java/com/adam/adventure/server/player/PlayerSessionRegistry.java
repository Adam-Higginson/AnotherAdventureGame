package com.adam.adventure.server.player;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;

import javax.inject.Inject;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class PlayerSessionRegistry {
    private final EntityFactory entityFactory;
    private final AtomicInteger playerIdSequence;
    private final Map<Integer, PlayerSession> playerIdToSession;

    @Inject
    public PlayerSessionRegistry(final EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
        this.playerIdSequence = new AtomicInteger(0);
        this.playerIdToSession = new ConcurrentHashMap<>();
    }


    public PlayerSession addPlayer(final String username, final InetAddress address, final int port) {
        final int playerId = playerIdSequence.incrementAndGet();
        LOG.info("Adding player: {} with id: {}", username, playerId);

        final Entity playerEntity = entityFactory.create("Player-" + playerId);
        playerEntity.setTransform(buildRandomPositionMatrix());

        final PlayerSession playerSession = PlayerSession.builder()
                .playerEntity(playerEntity)
                .address(address)
                .port(port)
                .id(playerId)
                .username(username)
                .lastReceivedUpdate(LocalDateTime.now())
                .state(PlayerSession.State.LOGGING_IN)
                .build();

        playerIdToSession.put(playerId, playerSession);
        return playerSession;
    }

    public void updatePlayerState(final int playerId, final PlayerSession.State newState) {
        final PlayerSession playerSession = playerIdToSession.remove(playerId);
        final PlayerSession newPlayerSession = PlayerSession.builder(playerSession)
                .state(newState)
                .build();
        playerIdToSession.put(playerId, newPlayerSession);
    }


    public List<PlayerSession> getPlayerSessionsWithState(final PlayerSession.State playerSessionState) {
        return playerIdToSession.values().stream()
                .filter(session -> session.getState() == playerSessionState)
                .collect(Collectors.toList());
    }

    private Matrix4f buildRandomPositionMatrix() {
        return new Matrix4f().translate(ThreadLocalRandom.current().nextInt(-500, 500),
                ThreadLocalRandom.current().nextInt(-500, 500),
                0);
    }


}
