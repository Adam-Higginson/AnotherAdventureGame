package com.adam.adventure.server.player;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import com.adam.adventure.lib.flatbuffer.schema.Player;
import com.adam.adventure.server.PlayerManager;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Matrix4f;

import javax.inject.Inject;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class PlayerSessionRegistry {
    private final EntityFactory entityFactory;
    private final AtomicInteger playerIdSequence;
    private final Map<Integer, PlayerSession> playerData;

    @Inject
    public PlayerSessionRegistry(final EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
        this.playerIdSequence = new AtomicInteger(0);
        this.playerData = new ConcurrentHashMap<>();
    }


    public PlayerSession addPlayer(final String username, final InetAddress address, final int port) {
        int playerId = playerIdSequence.incrementAndGet();
        LOG.info("Adding player: {} with id: {}", username, playerId);

        final Entity playerEntity = entityFactory.create("Player-" + playerId);
        playerEntity.setTransform(buildRandomPositionMatrix());

        PlayerSession playerSession = PlayerSession.builder()
                .playerEntity(playerEntity)
                .address(address)
                .port(port)
                .id(playerId)
                .username(username)
                .lastReceivedUpdate(LocalDateTime.now())
                .build();

        playerData.put(playerId, playerSession);

        return playerSession;
    }


    private Matrix4f buildRandomPositionMatrix() {
        return new Matrix4f().translate(ThreadLocalRandom.current().nextInt(0, 500),
                ThreadLocalRandom.current().nextInt(0, 500),
                0);
    }


    @Builder
    @Getter
    public static class PlayerSession {
        private Entity playerEntity;
        private InetAddress address;
        private int port;
        private int id;
        private String username;
        private LocalDateTime lastReceivedUpdate;
    }
}
