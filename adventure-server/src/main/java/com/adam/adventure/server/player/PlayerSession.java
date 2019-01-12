package com.adam.adventure.server.player;

import com.adam.adventure.entity.Entity;
import lombok.Getter;

import java.net.InetAddress;
import java.time.LocalDateTime;

@Getter
public class PlayerSession {
    private PlayerSession(final Builder builder) {
        playerEntity = builder.playerEntity;
        address = builder.address;
        port = builder.port;
        id = builder.id;
        username = builder.username;
        lastReceivedUpdate = builder.lastReceivedUpdate;
        state = builder.state;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final PlayerSession copy) {
        final Builder builder = new Builder();
        builder.playerEntity = copy.getPlayerEntity();
        builder.address = copy.getAddress();
        builder.port = copy.getPort();
        builder.id = copy.getId();
        builder.username = copy.getUsername();
        builder.lastReceivedUpdate = copy.getLastReceivedUpdate();
        builder.state = copy.getState();
        return builder;
    }

    public enum State {
        /**
         * The player has sent a login packet and the server may have sent a login success packet. The server is now
         * waiting for a client ready packet before sending updates.
         */
        LOGGING_IN,
        /**
         * The player has sent a client ready packet and is now accepting world state updates.
         */
        ACTIVE
    }

    private final Entity playerEntity;
    private final InetAddress address;
    private final int port;
    private final int id;
    private final String username;
    private final LocalDateTime lastReceivedUpdate;
    private final State state;


    public static final class Builder {
        private Entity playerEntity;
        private InetAddress address;
        private int port;
        private int id;
        private String username;
        private LocalDateTime lastReceivedUpdate;
        private State state;

        private Builder() {
        }

        public Builder playerEntity(final Entity playerEntity) {
            this.playerEntity = playerEntity;
            return this;
        }

        public Builder address(final InetAddress address) {
            this.address = address;
            return this;
        }

        public Builder port(final int port) {
            this.port = port;
            return this;
        }

        public Builder id(final int id) {
            this.id = id;
            return this;
        }

        public Builder username(final String username) {
            this.username = username;
            return this;
        }

        public Builder lastReceivedUpdate(final LocalDateTime lastReceivedUpdate) {
            this.lastReceivedUpdate = lastReceivedUpdate;
            return this;
        }

        public Builder state(final State state) {
            this.state = state;
            return this;
        }

        public PlayerSession build() {
            return new PlayerSession(this);
        }
    }
}
