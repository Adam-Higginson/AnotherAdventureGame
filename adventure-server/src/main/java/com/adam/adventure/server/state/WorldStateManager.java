package com.adam.adventure.server.state;

import com.adam.adventure.domain.WorldState;
import com.adam.adventure.event.EventBus;
import com.adam.adventure.event.EventSubscribe;
import com.adam.adventure.server.PlayerManager;
import com.adam.adventure.server.event.NewPlayerEvent;

import javax.inject.Inject;

public class WorldStateManager {

    private final PlayerManager playerManager;
    private final WorldState worldState;

    @Inject
    public WorldStateManager(final EventBus eventBus, PlayerManager playerManager) {
        this.playerManager = playerManager;
        this.worldState = new WorldState();
        eventBus.register(this);
    }

    public WorldState getWorldState() {
        return worldState;
    }
}
