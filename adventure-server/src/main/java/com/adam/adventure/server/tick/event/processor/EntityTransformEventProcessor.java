package com.adam.adventure.server.tick.event.processor;

import com.adam.adventure.server.player.PlayerSession;
import com.adam.adventure.server.player.PlayerSessionRegistry;
import com.adam.adventure.server.tick.event.EntityTransformEvent;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.function.Consumer;

@Slf4j
public class EntityTransformEventProcessor implements Consumer<EntityTransformEvent> {

    private final PlayerSessionRegistry playerSessionRegistry;

    @Inject
    EntityTransformEventProcessor(final PlayerSessionRegistry playerSessionRegistry) {
        this.playerSessionRegistry = playerSessionRegistry;
    }


    @Override
    public void accept(final EntityTransformEvent entityTransformEvent) {
        //For now assume entity must be a player
        playerSessionRegistry.getById(entityTransformEvent.getEntityId())
                .ifPresentOrElse(playerSession -> updateTransformAndLastModifiedTime(playerSession, entityTransformEvent),
                        () -> LOG.error("Received player id of: {} but could not find this player", entityTransformEvent.getEntityId()));
    }

    private void updateTransformAndLastModifiedTime(final PlayerSession playerSession, final EntityTransformEvent entityTransformEvent) {
        playerSession.getPlayerEntity().getTransform().set(entityTransformEvent.getTransform());
    }
}
