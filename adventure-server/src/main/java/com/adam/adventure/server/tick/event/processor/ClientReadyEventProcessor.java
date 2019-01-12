package com.adam.adventure.server.tick.event.processor;

import com.adam.adventure.server.event.ClientReadyEvent;
import com.adam.adventure.server.player.PlayerSession;
import com.adam.adventure.server.player.PlayerSessionRegistry;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.function.Consumer;

@Slf4j
public class ClientReadyEventProcessor implements Consumer<ClientReadyEvent> {
    private final PlayerSessionRegistry playerSessionRegistry;

    @Inject
    ClientReadyEventProcessor(final PlayerSessionRegistry playerSessionRegistry) {
        this.playerSessionRegistry = playerSessionRegistry;
    }

    @Override
    public void accept(final ClientReadyEvent clientReadyEvent) {
        LOG.info("Setting state for player id: {} as ACTIVE", clientReadyEvent.getPlayerId());
        playerSessionRegistry.updatePlayerState(clientReadyEvent.getPlayerId(), PlayerSession.State.ACTIVE);
    }
}
