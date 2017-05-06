package com.adam.adventure.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingEventListener implements EventListener {
    private final Logger LOG = LoggerFactory.getLogger(LoggingEventListener.class);

    @Override
    public void onEvent(final Event event) {
        LOG.debug("New event published of type: {}", event.getEventType());
    }

    public static LoggingEventListener registerNewLoggingEventListener(final EventBus eventBus) {
        final LoggingEventListener loggingEventListener = new LoggingEventListener();
        eventBus.registerEventListener(loggingEventListener);
        return loggingEventListener;
    }
}
