package com.adam.adventure.server;

import com.adam.adventure.entity.Entity;
import com.adam.adventure.entity.EntityFactory;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerManager {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerManager.class);

    private final AtomicInteger playerIdSequence;
    private final Map<Integer, PlayerTableRow> playerData;
    private EntityFactory entityFactory;

    @Inject
    public PlayerManager(final EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
        this.playerIdSequence = new AtomicInteger(0);
        this.playerData = new ConcurrentHashMap<>();
    }

    public PlayerTableRow addPlayer(final String username, final InetAddress address, final int port) {
        int playerId = playerIdSequence.incrementAndGet();
        LOG.info("Adding player: {} with id: {}", username, playerId);

        final Entity playerEntity = entityFactory.create("Player-" + playerId);
        playerEntity.setTransform(buildRandomPositionMatrix());

        PlayerTableRow playerTableRow = PlayerTableRow.newBuilder()
                .withPlayerEntity(playerEntity)
                .withAddress(address)
                .withPort(port)
                .withId(playerId)
                .withUsername(username)
                .withLastReceivedUpdate(LocalDateTime.now())
                .build();

        playerData.put(playerId, playerTableRow);
        return playerTableRow;
    }

    private Matrix4f buildRandomPositionMatrix() {
        return new Matrix4f().translate(ThreadLocalRandom.current().nextInt(0, 500),
                ThreadLocalRandom.current().nextInt(0, 500),
                0);
    }


    public static class PlayerTableRow {
        private Entity playerEntity;
        private InetAddress address;
        private int port;
        private int id;
        private String username;
        private LocalDateTime lastReceivedUpdate;

        private PlayerTableRow(Builder builder) {
            playerEntity = builder.playerEntity;
            address = builder.address;
            port = builder.port;
            id = builder.id;
            username = builder.username;
            lastReceivedUpdate = builder.lastReceivedUpdate;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public static Builder newBuilder(PlayerTableRow copy) {
            Builder builder = new Builder();
            builder.playerEntity = copy.getPlayerEntity();
            builder.address = copy.getAddress();
            builder.port = copy.getPort();
            builder.id = copy.getId();
            builder.username = copy.getUsername();
            builder.lastReceivedUpdate = copy.getLastReceivedUpdate();
            return builder;
        }


        public Entity getPlayerEntity() {
            return playerEntity;
        }

        public InetAddress getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public LocalDateTime getLastReceivedUpdate() {
            return lastReceivedUpdate;
        }

        public static final class Builder {
            private Entity playerEntity;
            private InetAddress address;
            private int port;
            private int id;
            private String username;
            private LocalDateTime lastReceivedUpdate;

            private Builder() {
            }

            public Builder withPlayerEntity(Entity playerEntity) {
                this.playerEntity = playerEntity;
                return this;
            }

            public Builder withAddress(InetAddress address) {
                this.address = address;
                return this;
            }

            public Builder withPort(int port) {
                this.port = port;
                return this;
            }

            public Builder withId(int id) {
                this.id = id;
                return this;
            }

            public Builder withUsername(String username) {
                this.username = username;
                return this;
            }

            public Builder withLastReceivedUpdate(LocalDateTime lastReceivedUpdate) {
                this.lastReceivedUpdate = lastReceivedUpdate;
                return this;
            }

            public PlayerTableRow build() {
                return new PlayerTableRow(this);
            }
        }
    }
}
