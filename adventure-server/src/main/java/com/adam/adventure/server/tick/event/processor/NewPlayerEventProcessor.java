package com.adam.adventure.server.tick.event.processor;

import com.adam.adventure.event.EventBus;
import com.adam.adventure.server.event.NewPlayerEvent;
import com.adam.adventure.server.player.PlayerSessionRegistry;
import com.adam.adventure.server.tick.event.PlayerSessionRegisteredEvent;

import javax.inject.Inject;
import java.util.function.Consumer;

class NewPlayerEventProcessor implements Consumer<NewPlayerEvent> {

    private final EventBus eventBus;
    private final PlayerSessionRegistry playerSessionRegistry;

    @Inject
    NewPlayerEventProcessor(final EventBus eventBus, final PlayerSessionRegistry playerSessionRegistry) {
        this.eventBus = eventBus;
        this.playerSessionRegistry = playerSessionRegistry;
    }

    @Override
    public void accept(final NewPlayerEvent newPlayerEvent) {
        PlayerSessionRegistry.PlayerSession playerSession = playerSessionRegistry.addPlayer(newPlayerEvent.getUsername(),
                newPlayerEvent.getAddress(),
                newPlayerEvent.getPort());
        eventBus.publishEvent(new PlayerSessionRegisteredEvent(playerSession));
    }
}
