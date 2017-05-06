package com.adam.adventure.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingGameStateListener implements GameStateListener {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingGameStateListener.class);

    @Override
    public void onStateTransition(final GameState oldState, final GameState newState) {
        LOG.debug("State transition from: {} to: {} detected", oldState, newState);
    }
}
