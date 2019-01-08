package com.adam.adventure.server.event;

import com.adam.adventure.event.Event;

public class NewPlayerEvent extends Event {
    private final int playerId;

    public NewPlayerEvent(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }
}
