package com.adam.adventure.server.tick.event;

import com.adam.adventure.server.player.PlayerSessionRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A player has been registered successfully.
 */
@AllArgsConstructor
@Getter
public class PlayerSessionRegisteredEvent extends ServerTickEvent {
    private final PlayerSessionRegistry.PlayerSession playerSession;
}
