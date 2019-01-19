package com.adam.adventure.server.event;

import com.adam.adventure.server.tick.event.ServerTickEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class ClientReadyEvent extends ServerTickEvent {
    private final UUID playerId;
}
