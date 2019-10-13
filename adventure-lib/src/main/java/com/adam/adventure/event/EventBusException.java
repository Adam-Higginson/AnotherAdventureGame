package com.adam.adventure.event;

public class EventBusException extends RuntimeException {
    public EventBusException(final Throwable cause) {
        super(cause);
    }
}
