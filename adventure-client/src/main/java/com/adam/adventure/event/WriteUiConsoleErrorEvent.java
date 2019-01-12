package com.adam.adventure.event;

import com.adam.adventure.entity.component.console.ConsoleEvent;
import com.adam.adventure.entity.component.console.UiConsoleComponent;

public class WriteUiConsoleErrorEvent extends ConsoleEvent {
    private final String message;

    public WriteUiConsoleErrorEvent(final String message) {
        this.message = "[ERROR] " + message;
    }


    @Override
    public void handle(final UiConsoleComponent console) {
        console.writeError(message);
    }
}