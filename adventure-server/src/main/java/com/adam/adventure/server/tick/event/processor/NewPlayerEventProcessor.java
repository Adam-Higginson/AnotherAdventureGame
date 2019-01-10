package com.adam.adventure.server.tick.event.processor;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.server.event.NewPlayerEvent;
import com.adam.adventure.server.player.PlayerSessionRegistry;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.function.Consumer;

@Slf4j
class NewPlayerEventProcessor implements Consumer<NewPlayerEvent> {

    private final PlayerSessionRegistry playerSessionRegistry;

    @Inject
    NewPlayerEventProcessor(final PlayerSessionRegistry playerSessionRegistry) {
        this.playerSessionRegistry = playerSessionRegistry;
    }

    @Override
    public void accept(final NewPlayerEvent newPlayerEvent) {
        PlayerSessionRegistry.PlayerSession playerSession = playerSessionRegistry.addPlayer(newPlayerEvent.getUsername(),
                newPlayerEvent.getAddress(),
                newPlayerEvent.getPort());
        LOG.info("Registered player for username: {} with id: {}", playerSession.getUsername(), playerSession.getId());
        //Next tick the server sends the world state to the player
    }
}
