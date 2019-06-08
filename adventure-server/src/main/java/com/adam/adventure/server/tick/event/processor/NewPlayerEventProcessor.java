package com.adam.adventure.server.tick.event.processor;

import com.adam.adventure.scene.SceneManager;
import com.adam.adventure.server.event.NewPlayerEvent;
import com.adam.adventure.server.player.PlayerSession;
import com.adam.adventure.server.player.PlayerSessionRegistry;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.function.Consumer;

@Slf4j
class NewPlayerEventProcessor implements Consumer<NewPlayerEvent> {

    private final PlayerSessionRegistry playerSessionRegistry;
    private final SceneManager sceneManager;

    @Inject
    NewPlayerEventProcessor(final PlayerSessionRegistry playerSessionRegistry, final SceneManager sceneManager) {
        this.playerSessionRegistry = playerSessionRegistry;
        this.sceneManager = sceneManager;
    }

    @Override
    public void accept(final NewPlayerEvent newPlayerEvent) {
        final PlayerSession playerSession = playerSessionRegistry.addPlayer(newPlayerEvent.getUsername(),
                newPlayerEvent.getAddress(),
                newPlayerEvent.getPort());
        sceneManager.getCurrentScene().ifPresentOrElse(scene -> scene.addEntity(playerSession.getPlayerEntity()),
                () -> LOG.error("Player joined but no current scene could be found!"));
        LOG.info("Registered player for username: {} with id: {}", playerSession.getUsername(), playerSession.getId());
        //Next tick the server sends the world state to the player
    }
}
