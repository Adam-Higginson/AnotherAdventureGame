package com.adam.adventure.event;

public class WriteUiConsoleErrorEvent extends Event {
    private final String message;

    public WriteUiConsoleErrorEvent(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
