package com.adam.adventure.state;

public interface GameStateListener {
    void onStateTransition(final GameState oldState, final GameState newState);
}
